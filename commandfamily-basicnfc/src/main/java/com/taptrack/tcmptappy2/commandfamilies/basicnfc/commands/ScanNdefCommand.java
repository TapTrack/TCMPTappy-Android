package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

/**
 * Command to instruct the Tappy to scan for NDEF-formatted tags.
 * If the Tappy detects a valid tag, it will stop scanning.
 *
 * Timeout values of 0 correspond to indefinite scanning
 */
public class ScanNdefCommand extends AbstractPollingCommand {
    public static final byte COMMAND_CODE = (byte)0x04;

    public ScanNdefCommand() {
        super();
    }

    public ScanNdefCommand(byte timeout, byte pollingMode) {
        super(timeout, pollingMode);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
