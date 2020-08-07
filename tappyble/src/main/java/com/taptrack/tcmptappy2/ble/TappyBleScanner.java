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

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import com.taptrack.tcmptappy.blescannercompat.BluetoothLeScannerCompat;
import com.taptrack.tcmptappy.blescannercompat.IBluetoothLeScanner;
import com.taptrack.tcmptappy.blescannercompat.ScanCallback;
import com.taptrack.tcmptappy.blescannercompat.ScanFilter;
import com.taptrack.tcmptappy.blescannercompat.ScanRecord;
import com.taptrack.tcmptappy.blescannercompat.ScanResult;
import com.taptrack.tcmptappy.blescannercompat.ScanSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TappyBleScanner {
    private final IBluetoothLeScanner scanner;
    private final Set<TappyBleFoundListener> listeners =
            Collections.newSetFromMap(new ConcurrentHashMap<TappyBleFoundListener, Boolean>(1));

    private ScanSettings settings;
    private AtomicBoolean isScanning = new AtomicBoolean(false);

    private final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            ScanRecord record = result.getScanRecord();
            if (record != null) {
                List<ParcelUuid> serviceUUIDs = record.getServiceUuids();
                for (ParcelUuid uuid : serviceUUIDs) {
                    if (uuid.getUuid().equals(TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID)) {
                        TappyBleDeviceDefinition deviceDefinition = new ParcelableTappyBleDeviceDefinition(
                                device.getName(),
                                device.getAddress(),
                                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID,
                                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_RX_CHARACTERISTIC_UUID,
                                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_TX_CHARACTERISTIC_UUID
                        );
                        newTappyFound(deviceDefinition);
                    } else if (uuid.getUuid().equals(TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID_V5)) {
                        TappyBleDeviceDefinition deviceDefinition = new ParcelableTappyBleDeviceDefinition(
                                device.getName(),
                                device.getAddress(),
                                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID_V5,
                                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_RX_CHARACTERISTIC_UUID_V5,
                                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_TX_CHARACTERISTIC_UUID_V5
                        );
                        newTappyFound(deviceDefinition);
                    }
                }

            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private TappyBleScanner(IBluetoothLeScanner scanner) {
        this.scanner = scanner;
        this.settings = new ScanSettings.Builder().build();
    }

    public static TappyBleScanner get() {
        return new TappyBleScanner(BluetoothLeScannerCompat.getBluetoothLeScanner());
    }

    public void startScan() {
        if(!isScanning.getAndSet(true)) {
            scanner.startScan(getCompatScanFilter(), settings, callback);
        }
    }

    public void stopScan() {
        if(isScanning.getAndSet(false)) {
            scanner.stopScan(callback);
        }
    }

    protected void newTappyFound(TappyBleDeviceDefinition tappyBle) {
        for(TappyBleFoundListener listener : listeners) {
            listener.onTappyBleFound(tappyBle);
        }
    }

    public void registerTappyBleFoundListener(TappyBleFoundListener listener) {
        listeners.add(listener);
    }

    public void unregisterTappyBleFoundListener(TappyBleFoundListener listener) {
        listeners.remove(listener);
    }

    public static List<com.taptrack.tcmptappy.blescannercompat.ScanFilter> getCompatScanFilter() {
        List<ScanFilter> list = new ArrayList<>(2);

        ScanFilter.Builder origBuilder =
                new ScanFilter.Builder();
        origBuilder.setServiceUuid(new ParcelUuid(TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID));
        list.add(origBuilder.build());

        ScanFilter.Builder v5Builder = new ScanFilter.Builder();
        v5Builder.setServiceUuid(new ParcelUuid(TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID_V5));
        list.add(v5Builder.build());
        return list;
    }


}
