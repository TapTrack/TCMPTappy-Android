package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes;
import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.LockingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.PollingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Command the Tappy to write a URI to a tag using an NDEF record
 */
public class WriteNdefUriRecordCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x05;
    protected byte timeout;
    protected byte lockflag; //1 to lock the flag
    protected byte uriCode;
    protected byte[] uri;

    public WriteNdefUriRecordCommand() {
        timeout = (byte) 0x00;
        lockflag = (byte) 0x00;
        uriCode = NdefUriCodes.URICODE_NOPREFIX;
        uri = new byte[0];
    }

    /**
     *
     * @param timeout
     * @param lockTag {@link PollingModes}
     * @param uriCode {@link NdefUriCodes}
     * @param uri
     */
    public WriteNdefUriRecordCommand(byte timeout, boolean lockTag, byte uriCode, byte[] uri) {
        this.timeout = timeout;
        this.lockflag = (byte) (lockTag ? 0x01: 0x00);
        this.uri = uri;
        this.uriCode = uriCode;
    }

    /**
     *
     * @param timeout
     * @param lockflag {@link PollingModes}
     * @param uriCode {@link NdefUriCodes}
     * @param uri
     */
    public WriteNdefUriRecordCommand(byte timeout, byte lockflag, byte uriCode, byte[] uri) {
        this.timeout = timeout;
        this.lockflag = lockflag;
        this.uriCode = uriCode;
        this.uri = uri;
    }

    public WriteNdefUriRecordCommand(byte timeout, boolean lockflag, byte uriCode, String uri) {
        this(timeout,lockflag,uriCode,uri.getBytes());
    }
    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length >= 3) {
            timeout = payload[0];
            lockflag = payload[1];
            uriCode = payload[2];
            if(payload.length > 3) {
                uri = Arrays.copyOfRange(payload, 3, payload.length);
            }
            else {
                uri = new byte[0];
            }
        }
        else {
            throw new MalformedPayloadException("Invalid raw message");
        }
    }

    /**
     * Retreive the timeout after which the Tappy will stop scanning and send a
     * {@link ScanTimeoutResponse}
     *
     * 0x00 disables timeout
     * @return
     */
    public byte getTimeout() {
        return timeout;
    }

    /**
     * Set the timeout after which the Tappy will stop scanning and send a
     * {@link ScanTimeoutResponse}
     *
     * 0x00 disables timeout
     * @param timeout
     */
    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    /**
     * Get the flag that determines if the Tappy will attempt to lock the tag after writing
     *
     * See: {@link LockingModes}
     * @return locking flag
     */
    public byte getLockflag() {
        return lockflag;
    }

    /**
     * Set the flag that determines if the Tappy will attempt to lock the tag after writing
     *
     * See: {@link LockingModes}
     * @param lockflag locking mode this command should be executed with
     */
    public void setLockflag(byte lockflag) {
        this.lockflag = lockflag;
    }

    /**
     * If the current state of the locking tag will lock the tag
     * @return
     */
    public boolean willLock() {
        return lockflag == LockingModes.LOCK_TAG;
    }

    /**
     * Set locking flag to the appropriate value for a locking state
     * @param lockTag
     */
    public void setToLock(boolean lockTag) {
        this.lockflag = (byte) (lockTag ? LockingModes.LOCK_TAG:LockingModes.DONT_LOCK);
    }

    public byte[] getUriBytes() {
        return uri;
    }

    public String getUri() {
        return new String(uri);
    }

    public void setUri(byte[] uri) {
        this.uri = uri;
    }

    public void setUri(String uri) {
        this.uri = uri.getBytes();
    }

    /**
     * Get the NDEF Uri this command will use
     *
     * See: {@link NdefUriCodes}
     * @return current code
     */
    public byte getUriCode() {
        return uriCode;
    }

    /**
     * Set the URI code for this NDEF write
     *
     * See: {@link NdefUriCodes}
     * @param uriCode new uri code
     */
    public void setUriCode(byte uriCode) {
        this.uriCode = uriCode;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(3+uri.length);
        outputStream.write(timeout);
        outputStream.write(lockflag);
        outputStream.write(uriCode);
        try {
            outputStream.write(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
