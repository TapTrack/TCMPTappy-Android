package com.taptrack.tcmptappy2;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A generic representation of a TCMP Tappy device. It provides a common
 * interface for dealing with connection/disconnection, as well as status
 * monitoring and transceiving TCMP messages.
 */
public interface Tappy {
    /**
     * Represents a state of the Tappy object. Note that implementations may skip
     * some of these states. For example, if a USB Tappy is unplugged, the instance
     * may transition from STATUS_READY to STATUS_DISCONNECTED without ever entering
     * STATUS_DISCONNECTED
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_DISCONNECTING, STATUS_DISCONNECTED, STATUS_CONNECTING, STATUS_READY, STATUS_CLOSED, STATUS_ERROR})
    public @interface TappyStatus {
    }

    /**
     * This Tappy instance currently doesn't think it has an active connection to
     * a Tappy device
     */
    public int STATUS_DISCONNECTED = 0;

    /**
     * This Tappy instance currently is establishing an active connection to
     * a Tappy device, but is not ready to transceive data yet.
     * <p>
     * For example, establishing a connection with a BLE device is a multi-step process that
     * takes a noticeable amount of time since the Android device must connect, discover services,
     * and then subscribe to the Tappy's TX characteristic before it is ready to do anything. During
     * this time, the implementation should be in a state of STATUS_CONNECTING
     */
    public int STATUS_CONNECTING = 1;

    /**
     * This Tappy instance believes it is ready to transceive data to a Tappy device.
     * <p>
     * It should be noted that this does not guarantee that the Tappy is ready. For instance,
     * it takes Android's BLE stack a very long time (on the order of 30s) to notify applications
     * when a BLE device disconnects. During this time the Tappy instance will still think it is
     * 'READY' despite being unable to communicate.
     * <p>
     * Additionally, there is a hysteresis effect in both TappyUSB and TappyBLE devices where the
     * USB/BLE modules will finish initializing and be ready to connect before the Tappy's main
     * logic controller has completed its bootup process. This can hypothetically result in a
     * Tappy instance being READY before the Tappy itself is READY. In practise, this is almost
     * impossible to observe with BLE Tappies, but since USB Tappies are powered by the connection
     * itself, it can become noticeable on such devices.
     */
    public int STATUS_READY = 2;

    /**
     * This Tappy is currently in the process of disconnecting.
     * <p>
     * Do not count on a Tappy passing through this state as prior to proceeding to
     * reaching {@value #STATUS_DISCONNECTED}. In circumstances like a USB reader being unplugged,
     * the Tappy may transition straight to {@value #STATUS_DISCONNECTED} from whatever state it is
     * currently in.
     */
    public int STATUS_DISCONNECTING = 3;

    /**
     * This Tappy connection has closed and all resources it was holding are freed. You should not
     * attempt to reconnect after reaching STATUS_CLOSED
     */
    public int STATUS_CLOSED = 4;

    /**
     * An error has occurred in the communicator. In general, this means an unrecoverable illegal
     * state has occurred. After receiving this state, you should call {@link Tappy#close()} and
     * attempt to connect anew instead of trying to reconnect with this Tappy instance.
     */
    public int STATUS_ERROR = 5;

    /**
     * Interface definition for a callback to be invoked when a valid {@link TCMPMessage} is
     * received from the Tappy. Be aware that this callback may be called from an arbitrary
     * thread.
     */
    interface ResponseListener {
        /**
         * Called when a response has been received. May be called on an arbitrary thread.
         *
         * @param message The message that was received
         */
        void responseReceived(@NonNull TCMPMessage message);
    }

    /**
     * Interface definition for a callback to be invoked when a new {@link TappyStatus} is
     * received from the Tappy. Be aware that this callback may be called from an arbitrary
     * thread.
     */
    interface StatusListener {
        /**
         * Called when a new {@link TappyStatus} is received from the Tappy. May be called
         * from an arbitrary thread.
         *
         * @param status The status that was received
         */
        void statusReceived(@TappyStatus int status);
    }

    /**
     * Interface definition for a callback to be invoked when a new packet is
     * received from the Tappy, but it has failed to parse correctly. If this occurs,
     * it generally indicates one of four conditions:
     * <ol>
     * <l1>
     * there is a large amount of noise is disrupting the connection
     * </l1>
     * <l1>
     * the device being connected to isn't actually a Tappy
     * </l1>
     * <l1>
     * the Tappy has entered a very abnormal error state
     * </l1>
     * <l1>
     * you received a fragment of a message because the connection completed in the middle
     * of a send operation
     * </l1>
     * </ol>
     * <p>
     * Be aware that this callback may be called from an arbitrary thread.
     */
    interface UnparsablePacketListener {
        /**
         * Called when an unparsable packet has been received.
         *
         * @param packet The packet that was received
         */
        void unparsablePacketReceived(@NonNull byte[] packet);
    }

    /**
     * Registers a {@link ResponseListener} callback
     * to be called when this Tappy instance received a response
     * from the Tappy device.
     * <p>
     * Be aware that this callback may be called from an arbitrary thread.
     *
     * @param listener the listener to register
     * @see Tappy#unregisterResponseListener(ResponseListener)
     * @see ResponseListener
     */
    public void registerResponseListener(@NonNull ResponseListener listener);

    /**
     * Removes a {@link ResponseListener} callback from the collection that is called
     * when this Tappy receives a response.
     *
     * @param listener the listener to unregister
     * @see Tappy#registerResponseListener(ResponseListener)
     * @see ResponseListener
     */
    public void unregisterResponseListener(@NonNull ResponseListener listener);

    /**
     * Registers a {@link StatusListener} callback
     * to be called after this Tappy transitions to a new status.
     * <p>
     * Be aware that this callback may be called from an arbitrary thread.
     *
     * @param listener the listener to register
     * @see Tappy#unregisterStatusListener(StatusListener)
     * @see StatusListener
     */
    public void registerStatusListener(@NonNull StatusListener listener);

    /**
     * Removes a {@link StatusListener} callback from the collection that is called
     * after this Tappy transitions to a new status.
     *
     * @param listener the listener to unregister
     * @see Tappy#registerStatusListener(StatusListener)
     * @see StatusListener
     */
    public void unregisterStatusListener(@NonNull StatusListener listener);

    /**
     * Registers a {@link UnparsablePacketListener} callback
     * to be called when this Tappy instance received an unparsable response
     * from the Tappy device.
     * <p>
     * Be aware that this callback may be called from an arbitrary thread.
     *
     * @param listener the listener to register
     * @see Tappy#unregisterUnparsablePacketListener(UnparsablePacketListener)
     * @see UnparsablePacketListener
     */
    public void registerUnparsablePacketListener(@NonNull UnparsablePacketListener listener);

    /**
     * Removes a {@link UnparsablePacketListener} callback from the collection that is called
     * when this Tappy receives an unparsable response.
     *
     * @param listener the listener to unregister
     * @see Tappy#registerUnparsablePacketListener(UnparsablePacketListener)
     * @see UnparsablePacketListener
     */
    public void unregisterUnparsablePacketListener(@NonNull UnparsablePacketListener listener);

    /**
     * Removes all {@link UnparsablePacketListener}, {@link StatusListener}, and
     * {@link ResponseListener} instances from this Tappy.
     *
     * @see Tappy#registerResponseListener(ResponseListener)
     * @see Tappy#unregisterResponseListener(ResponseListener)
     * @see Tappy#registerStatusListener(StatusListener)
     * @see Tappy#unregisterStatusListener(StatusListener)
     * @see Tappy#registerUnparsablePacketListener(UnparsablePacketListener)
     * @see Tappy#unregisterUnparsablePacketListener(UnparsablePacketListener)
     */
    public void removeAllListeners();

    /**
     * Request that the Tappy connect. Returns true
     * if it seems that the connection can proceed. Since this operation
     * is supposed to be non-blocking, but connecting (especially for BLE)
     * can take a very long time, one should treat this return value
     * as a mere pre-condition check.
     * <p>
     * Be aware that that this may return false if you call it when already connected.
     */
    boolean connect();

    /**
     * Send a TCMP message to the Tappy. Returns true
     * if the message was successfully prepared for sending.
     * It is important to note that this does not mean that it
     * was sent, rather that the connection appears to be in a
     * state that would allow sending.
     *
     * @param message message to send
     */
    boolean sendMessage(@NonNull TCMPMessage message);

    /**
     * Request that the Tappy disconnect. Returns true
     * if it seems that the disconnection can proceed. Since this operation
     * is supposed to be non-blocking, but may take a significant amount of time
     * for some communicators, one should treat this return value
     * as a mere pre-condition check.
     * <p>
     * Be aware that that this may return false if you call it when already disconnected.
     */
    boolean disconnect();

    /**
     * Request that the Tappy close. Returns true
     * if it seems that the close can proceed. Since this operation
     * is supposed to be non-blocking, but may take a significant amount of time
     * for some communicators, one should treat this return value
     * as a mere pre-condition check. Note that, even if the close fails, the
     * Tappy will close as many resources as it can.
     * <p>
     * Be aware that that this may return false if you call it when already closed.
     */
    boolean close();

    /**
     * Get a unique description of the Tappy. These descriptions should strive to be
     * human-readable, but they should favour uniqueness over aesthetic appeal. Note that
     * not all types of connections are capable of providing a truly unique Tappy description,
     * so while these should be unique at run-time, they should not be persisted long-term.
     * Specific Tappy implementation should provide greater detail on the description's uniqueless.
     *
     * @return string the description.
     */
    public String getDeviceDescription();

    /**
     * Retrieves whatever the latest status the Tappy entered into was
     *
     * @return the latest status.
     */
    @TappyStatus
    public int getLatestStatus();
}
