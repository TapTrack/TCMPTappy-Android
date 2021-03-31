package com.taptrack.tcmptappy2;

import androidx.annotation.NonNull;

/**
 * Interface definition that abstracts away the details of a transmission medium
 * for a {@link SerialTappy}.
 */
public interface TappySerialCommunicator {
    /**
     * Interface definition for a callback that is called when a communicator
     * receives bytes from the Tappy.
     */
    interface DataReceivedListener {
        /**
         * Called when data is received from the Tappy.
         *
         * @param data data that was received. May be of any length and called on an
         *             arbitrary thread.
         */
        void recievedBytes(@NonNull byte[] data);
    }

    /**
     * Sets the {@link DataReceivedListener} that will be called when this communicator
     * receives new data
     *
     * @param listener the listener to set
     */
    void setDataListener(@NonNull DataReceivedListener listener);

    /**
     * Remove the {@link DataReceivedListener} this communicator is currently calling,
     * if one is registered.
     */
    void removeDataListener();

    /**
     * Register a status listener to be notified of changes in the communicator's connection
     * state.
     *
     * @param listener the listener to register
     */
    void setStatusListener(@NonNull Tappy.StatusListener listener);

    /**
     * Remove the status listener registered on this communicator, if one is registered.
     */
    void removeStatusListener();

    /**
     * Retrieve the most recent status this communicator entered
     * @return a TappyStatus int
     */
    @Tappy.TappyStatus
    int getStatus();

    /**
     * Send raw binary data over the interface. Returns true
     * if the message was successfully prepared for sending.
     * It is important to note that this does not mean that it
     * was sent, rather that the connection appears to be in a
     * state that would allow connection.
     *
     * @param data data to send
     */
    boolean sendBytes(@NonNull byte[] data);

    /**
     * Request that the communicator connect. Returns true
     * if it seems that the connection can proceed. Since this operation
     * is supposed to be non-blocking, but connecting (especially for BLE)
     * can take a very long time, one should treat this return value
     * as a mere pre-condition check.
     *
     * Note that this may return false if you call it when already connected.
     */
    boolean connect();

    /**
     * Request that the communicator disconnect. Returns true
     * if it seems that the disconnection can proceed. Since this operation
     * is supposed to be non-blocking, but may take a significant amount of time
     * for some communicators, one should treat this return value
     * as a mere pre-condition check.
     *
     * Note that this may return false if you call it when already disconnected.
     */
    boolean disconnect();

    /**
     * Request that the communicator close. Returns true
     * if it seems that the close can proceed. Since this operation
     * is supposed to be non-blocking, but may take a significant amount of time
     * for some communicators, one should treat this return value
     * as a mere pre-condition check. Note that, even if the close fails, the
     * communicator will close as many resources as it can.
     *
     * Note that this may return false if you call it when already closed.
     */
    boolean close();

    /**
     * Should return a unique string describing the Tappy being
     * communicated with. This should aim to be relatively unique,
     * but the specific Tappy implementation should specify any caveats
     * on its uniqueness
     *
     * @return Tappy identifying string
     */
    String getDeviceDescription();
}
