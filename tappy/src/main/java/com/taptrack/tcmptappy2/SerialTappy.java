package com.taptrack.tcmptappy2;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This class represents a Tappy that is communicated with via standard TCMP over a
 * {@link TappySerialCommunicator}. You can use this directly with a custom {@link TappySerialCommunicator}
 * if you so choose, or use the standard TappyUSB and TappyBLE variants provided in other
 * packages in the SDK.
 */
public class SerialTappy implements Tappy {
    // This is a fairly arbitrary size at the moment, but it should easily cover most TCMP responses
    // besides ones things like NDEF read results.
    private static final int RECEIVE_BUFFER_INITIAL_SIZE = 40;

    private final Set<ResponseListener> responseListeners = new CopyOnWriteArraySet<>();
    private final Set<UnparsablePacketListener> unparsableListeners = new CopyOnWriteArraySet<>();
    private final Set<StatusListener> statusListeners = new CopyOnWriteArraySet<>();

    private final TappySerialCommunicator communicator;

    private ByteArrayOutputStream receiveBuffer = new ByteArrayOutputStream(RECEIVE_BUFFER_INITIAL_SIZE);
    private final Object receivedBufferLock = new Object();

    private final TappySerialCommunicator.DataReceivedListener dataReceivedListener = new TappySerialCommunicator.DataReceivedListener() {
        @Override
        public void recievedBytes(@NonNull byte[] data) {

            List<byte[]> commands = null;
            // TODO: make this more efficient
            // Switching over to the streaming approach the
            // JavaScript SDK uses should help
            synchronized (receivedBufferLock) {
                try {
                    receiveBuffer.write(data);
                } catch (IOException ignored) {
                    // this should be impossible
                }
                if(HDLCUtils.containsHdlcEndpoint(data)) {
                    byte[] currentBuffer = receiveBuffer.toByteArray();
                    HDLCParseResult parseResult = HDLCByteArrayParser.process(currentBuffer);
                    commands = parseResult.getPackets();
                    byte[] remainder = parseResult.getRemainder();
                    receiveBuffer = new ByteArrayOutputStream(remainder.length+RECEIVE_BUFFER_INITIAL_SIZE);
                    try {
                        receiveBuffer.write(remainder);
                    } catch (IOException ignored) {
                        // this should be impossible
                    }
                }
            }

            if(commands == null) {
                return;
            }

            for(byte[] hdlcPacket : commands) {
                try {
                    byte[] decodedPacket = HDLCUtils.hdlcDecodePacket(hdlcPacket);
                    if (decodedPacket.length != 0) {
                        RawTCMPMessage message = new RawTCMPMessage(decodedPacket);
                        notifyListenersOfMessage(message);
                    }
                } catch (IllegalHDLCFormatException | TCMPMessageParseException e) {
                    notifyListenersOfUnparsablePacket(hdlcPacket);
                }
            }

        }
    };

    /**
     * Construct a SerialTappy around a {@link TappySerialCommunicator}. You should
     * pass the communicator in in an unconnected state.
     *
     * @param communicator the communicator to use.
     */
    public SerialTappy(TappySerialCommunicator communicator) {
        this.communicator = communicator;

        communicator.setStatusListener(new StatusListener() {
            @Override
            public void statusReceived(@TappyStatus int status) {
                notifyListenersOfStatus(status);
            }
        });

        communicator.setDataListener(dataReceivedListener);
    }

    @Override
    public void registerResponseListener(@NonNull ResponseListener listener) {
        responseListeners.add(listener);
    }

    protected void notifyListenersOfMessage(@NonNull TCMPMessage message) {
        for(ResponseListener listener : responseListeners) {
            listener.responseReceived(message);
        }
    }

    @Override
    public void unregisterResponseListener(@NonNull ResponseListener listener) {
        responseListeners.remove(listener);
    }

    @Override
    public void registerStatusListener(@NonNull StatusListener listener) {
        statusListeners.add(listener);
    }

    protected void notifyListenersOfStatus(@Tappy.TappyStatus int status) {
        for(StatusListener listener : statusListeners) {
            listener.statusReceived(status);
        }
    }

    @Override
    public void unregisterStatusListener(@NonNull StatusListener listener) {
        statusListeners.remove(listener);
    }

    @Override
    public void registerUnparsablePacketListener(@NonNull UnparsablePacketListener listener) {
        unparsableListeners.add(listener);
    }

    protected void notifyListenersOfUnparsablePacket(byte[] packet) {
        for(UnparsablePacketListener listener : unparsableListeners) {
            listener.unparsablePacketReceived(packet);
        }
    }

    @Override
    public void unregisterUnparsablePacketListener(@NonNull UnparsablePacketListener listener) {
        unparsableListeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        responseListeners.clear();
        unparsableListeners.clear();
        statusListeners.clear();
    }

    @Override
    public boolean connect() {
        return communicator.connect();
    }

    @Override
    public boolean sendMessage(@NonNull TCMPMessage message) {
        return communicator.sendBytes(HDLCUtils.hdlcEncodePacket(message.toByteArray()));
    }

    @Override
    public boolean disconnect() {
        return communicator.disconnect();
    }

    @Override
    public boolean close() {
        return communicator.close();
    }

    @Override
    public String getDeviceDescription() {
        return communicator.getDeviceDescription();
    }

    @Override
    public int getLatestStatus() {
        return communicator.getStatus();
    }

    /**
     * Retrieve the backing {@link TappySerialCommunicator} used by this SerialTappy
     *
     * @return the backing communicator
     */
    @NonNull
    protected TappySerialCommunicator getCommunicator() {
        return communicator;
    }
}
