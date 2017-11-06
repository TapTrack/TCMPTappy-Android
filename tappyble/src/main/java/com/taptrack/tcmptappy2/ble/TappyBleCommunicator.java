/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy2.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.taptrack.tcmptappy2.Tappy;
import com.taptrack.tcmptappy2.TappySerialCommunicator;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class TappyBleCommunicator implements TappySerialCommunicator {
    private static final String TAG = TappyBleCommunicator.class.getName();
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    private final AtomicInteger state = new AtomicInteger(Tappy.STATUS_DISCONNECTED);

    private final Context ctx;

    private final TappyBleMtuChunkingStream mtuChunkingStream = new TappyBleMtuChunkingStream();

    private final AtomicReference<BluetoothManager> bluetoothManagerRef = new AtomicReference<>();
    private final AtomicReference<BluetoothAdapter> bluetoothAdapterRef = new AtomicReference<>();
    private final AtomicReference<BluetoothGatt> bluetoothGattRef = new AtomicReference<>();

    @NonNull
    private final String bleDeviceAddress;
    @NonNull
    private final UUID serialServiceUuid;
    @NonNull
    private final UUID rxCharactertisticUuid;
    @NonNull
    private final UUID txCharacteristicUuid;
    @NonNull
    private final String bleDeviceName;
    @Nullable
    private TappySerialCommunicator.DataReceivedListener dataReceivedListener;
    @Nullable
    private Tappy.StatusListener statusListener;

    private final AtomicBoolean isSending = new AtomicBoolean(false);
    // Whether or not we need to wait for descriptor write before going to STATUS_READY
    private boolean requiresDescriptorWrite = false;

    private boolean isInitialized = false;
    private final Object initializationLock = new Object();

    private boolean debug = false;

    private final Handler uiThreadHandler;

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(gatt.getDevice().getAddress().equals(bleDeviceAddress)) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    bluetoothGattRef.compareAndSet(null,gatt);
                    BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
                    debugLog("Connected to GATT server.");
                    // Attempts to discover services after successful connection.
                    if (bluetoothGatt != null) {
                        if(bluetoothGatt.discoverServices()) {
                            debugLog("Attempting to start service discovery");
                        } else {
                            Log.e(TAG,"Unable to discover services");
                            changeStateAndNotify(Tappy.STATUS_ERROR);
                        }
                    } else {
                        Log.wtf(TAG, "Somehow connected with no gatt");
                        changeStateAndNotify(Tappy.STATUS_ERROR);
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if(getStatus() != Tappy.STATUS_CLOSED) {
                        changeStateAndNotify(Tappy.STATUS_DISCONNECTED);
                        Log.i(TAG, "Disconnected from GATT server.");
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(gatt.getDevice().getAddress().equals(bleDeviceAddress)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    servicesDetected();
                } else {
                    changeStateAndNotify(Tappy.STATUS_ERROR);
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(gatt.getDevice().getAddress().equals(bleDeviceAddress)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    characteristicRead(characteristic);
                } else if (status == BluetoothGatt.GATT_CONNECTION_CONGESTED) {
                    // not logging an error as this may self resolve
                    // probably not a good practise
                    Log.e(TAG, "GATT CNXN CONGESTED");
                } else if (status == BluetoothGatt.GATT_FAILURE) {
                    // not logging an error as this may self resolve
                    // probably not a good practise, but google is super vague
                    // about what this actually means
                    Log.e(TAG, "GATT FAILURE");
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(gatt.getDevice().getAddress().equals(bleDeviceAddress)) {
                sendBytesFromBuffer();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if(gatt.getDevice().getAddress().equals(bleDeviceAddress)) {
                characteristicRead(characteristic);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor,
                                      int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if(gatt.getDevice().getAddress().equals(bleDeviceAddress)
                    && requiresDescriptorWrite
                    && descriptor.getUuid().equals(CLIENT_CHARACTERISTIC_CONFIG)) {
                debugLog("Client config descriptor written");
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    debugLog("Client config descriptor write successful");
                    conditionalChangeStateAndNotify(Tappy.STATUS_CONNECTING, Tappy.STATUS_READY);
                } else if (status == BluetoothGatt.GATT_FAILURE ||
                        status == BluetoothGatt.GATT_CONNECTION_CONGESTED ||
                        status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION ||
                        status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION ||
                        status == BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED ||
                        status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                    debugLog("Client config descriptor write failed");
                    changeStateAndNotify(Tappy.STATUS_ERROR);
                }
            }
        }
    };

    public TappyBleCommunicator(@NonNull Context ctx, @NonNull TappyBleDeviceDefinition deviceDefinition) {
        this(
                ctx,
                deviceDefinition.getAddress(),
                deviceDefinition.getName(),
                deviceDefinition.getSerialServiceUuid(),
                deviceDefinition.getTxCharacteristicUuid(),
                deviceDefinition.getRxCharacteristicUuid());
    }

    public TappyBleCommunicator(@NonNull Context ctx,
                                @NonNull String address,
                                @NonNull String bleDeviceName,
                                @NonNull UUID serialServiceUuid,
                                @NonNull UUID txCharacteristicUuid,
                                @NonNull UUID rxCharactertisticUuid) {
        this.ctx = ctx;
        this.uiThreadHandler = new Handler(ctx.getMainLooper());
        this.bleDeviceAddress = address;
        this.bleDeviceName = bleDeviceName;
        this.serialServiceUuid = serialServiceUuid;
        this.txCharacteristicUuid = txCharacteristicUuid;
        this.rxCharactertisticUuid = rxCharactertisticUuid;
    }

    private void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void debugLog(String message) {
        if(debug) {
            Log.d(TAG, message);
        }
    }

    public void debugLog(String message, Exception e) {
        if(debug) {
            Log.d(TAG, message, e);
        }
    }

    private void characteristicRead(BluetoothGattCharacteristic characteristic) {
        if(characteristic.getUuid().equals(txCharacteristicUuid)) {
            final byte[] data = characteristic.getValue();
            if(dataReceivedListener != null) {
                dataReceivedListener.recievedBytes(data);
            }
        }
    }

    
    private void notifyStateListener(@Tappy.TappyStatus int newState) {
        if(statusListener != null) {
            statusListener.statusReceived(newState);
        }
    }

    private boolean conditionalChangeStateAndNotify(@Tappy.TappyStatus int currentState, @Tappy.TappyStatus int newState) {
        boolean didChange = state.compareAndSet(currentState,newState);
        //noinspection WrongConstant
        if(didChange) {
            debugLog("State did change");
            notifyStateListener(newState);
        } else {
            debugLog("State did not change");
        }

        return didChange;
    }

    private void changeStateAndNotify(@Tappy.TappyStatus int newState) {
        state.set(newState);
        notifyStateListener(newState);
    }

    private void servicesDetected() {
        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
        if(bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(serialServiceUuid);
            BluetoothGattCharacteristic charac = service.getCharacteristic(txCharacteristicUuid);
            bluetoothGatt.setCharacteristicNotification(charac, true);

            BluetoothGattDescriptor descriptor = charac.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            if(descriptor != null) {
                requiresDescriptorWrite = true;
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                boolean success = bluetoothGatt.writeDescriptor(descriptor);
                if(!success) {
                    Log.e(TAG,"Client characteristic detected, but setting it failed");
                    changeStateAndNotify(Tappy.STATUS_ERROR);
                }
            } else {
                changeStateAndNotify(Tappy.STATUS_READY);
            }

        }
        else {
            Log.wtf(TAG,"Services detected with no gatt");
            changeStateAndNotify(Tappy.STATUS_ERROR);
        }
    }

    private boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        BluetoothManager bluetoothManager = bluetoothManagerRef.get();
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                changeStateAndNotify(Tappy.STATUS_ERROR);
                return false;
            }
            else {
                bluetoothManagerRef.set(bluetoothManager);
            }
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            changeStateAndNotify(Tappy.STATUS_ERROR);
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        else {
            bluetoothAdapterRef.set(bluetoothAdapter);
        }

        return true;
    }

    public boolean connect() {
        if(conditionalChangeStateAndNotify(Tappy.STATUS_DISCONNECTED,Tappy.STATUS_CONNECTING)) {
            if (!isInitialized) {
                synchronized (initializationLock) {
                    if (!isInitialized && !initialize()) {
                        return false;
                    } else {
                        isInitialized = true;
                    }
                }
            }

            BluetoothAdapter bluetoothAdapter = bluetoothAdapterRef.get();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
                changeStateAndNotify(Tappy.STATUS_ERROR);
                return false;
            }

            // Previously connected device.  Try to reconnect.
            BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
            if (bluetoothGatt != null) {
                debugLog("Trying to use an existing mBluetoothGatt for connection.");
                if (bluetoothGatt.connect()) {
                    //STATE STATUS_CONNECTING
                    return true;
                } else {
                    changeStateAndNotify(Tappy.STATUS_ERROR);
                    return false;
                }
            }

            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bleDeviceAddress);
            if (device == null) {
                Log.e(TAG, "Device not found. Unable to connect.");
                changeStateAndNotify(Tappy.STATUS_ERROR);
                return false;
            }

            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            // this supposedly has some strange behaviour on some devices
            debugLog("Trying to create a new connection.");
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    BluetoothGatt gatt = device.connectGatt(ctx, false, gattCallback);
                    bluetoothGattRef.set(gatt);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public boolean disconnect() {
        if(conditionalChangeStateAndNotify(Tappy.STATUS_READY,Tappy.STATUS_DISCONNECTING)) {
            BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
            if (bluetoothGatt == null) {
                Log.w(TAG, "Gatt not available to disconnect from");
                changeStateAndNotify(Tappy.STATUS_ERROR);
                return false;
            }
            bluetoothGatt.disconnect();
            return true;
        } else {
            return false;
        }
    }

    public boolean close() {
        changeStateAndNotify(Tappy.STATUS_CLOSED);

        BluetoothGatt gatt = bluetoothGattRef.get();
        if(gatt != null) {
            gatt.disconnect();
        }

        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
        if(bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGattRef.set(null);
        }
        return true;
    }

    @Override
    public String getDeviceDescription() {
        return "TAPPYBLE-"+bleDeviceName+"-"+bleDeviceAddress;
    }

    private void initiateSendIfNecessary() {
        if(!isSending.getAndSet(true)) {
            sendBytesFromBuffer();
        }
    }

    private void sendBytesFromBuffer() {
        int currentState = getStatus();

        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();

        mtuChunkingStream.lockRead();
        if(mtuChunkingStream.hasBytes() &&
                currentState == Tappy.STATUS_READY &&
                bluetoothGatt != null) {
            byte[] nextChunk = mtuChunkingStream.getNextChunk();

            BluetoothGattService service = bluetoothGatt.getService(serialServiceUuid);
            if (service == null) {
                throw new IllegalStateException("Trying to send to device without truconnect");
            }

            BluetoothGattCharacteristic charac = service.getCharacteristic(rxCharactertisticUuid);

            if (nextChunk.length > 0) {
                charac.setValue(nextChunk);
                bluetoothGatt.writeCharacteristic(charac);
            }
            else {
                isSending.set(false);
            }
        }
        else {
            isSending.set(false);
        }
        mtuChunkingStream.unlockRead();
    }

    @Override
    public void setDataListener(@NonNull DataReceivedListener listener) {
        dataReceivedListener = listener;
    }

    @Override
    public void removeDataListener() {
        dataReceivedListener = null;
    }

    @Override
    public void setStatusListener(@NonNull Tappy.StatusListener listener) {
        statusListener = listener;
    }

    @Override
    public void removeStatusListener() {
        statusListener = null;
    }

    @Override
    public int getStatus() {
        //noinspection WrongConstant
        return state.get();
    }

    @Override
    public boolean sendBytes(@NonNull byte[] data) {
        if(state.get() == Tappy.STATUS_READY) {
            mtuChunkingStream.writeToBuffer(data);
            initiateSendIfNecessary();
            return true;
        }
        return false;
    }

}
