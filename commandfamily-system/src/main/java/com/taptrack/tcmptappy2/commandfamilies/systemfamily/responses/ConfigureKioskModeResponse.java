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

package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ConfigureKioskModeCommand;
import com.taptrack.tcmptappy2.MalformedPayloadException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The Tappy's kiosk mode configuration has successfully been set
 */
public class ConfigureKioskModeResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte) 0x0C;

    private byte pollingSetting;
    private byte ndefSetting;
    private int heartbeatPeriod;
    private byte scanErrorSetting;

    private int successfulScanLedTimeout = ConfigureKioskModeCommand.DEFAULT_SUCCESSFUL_SCAN_LED_TIMEOUT;
    private boolean hasSuccessfulScanLedTimeout;

    private int failedScanLedTimeout = ConfigureKioskModeCommand.DEFAULT_FAILED_SCAN_LED_TIMEOUT;
    private boolean hasFailedScanLedTimeout;

    private int postScanDelayTimeout = ConfigureKioskModeCommand.DEFAULT_POST_SCAN_DELAY_TIMEOUT;
    private boolean hasPostScanDelayTimeout;

    public ConfigureKioskModeResponse() {
        pollingSetting = ConfigureKioskModeCommand.PollingSettings.NO_CHANGE;
        ndefSetting = ConfigureKioskModeCommand.NdefSettings.NO_CHANGE;
        heartbeatPeriod = ConfigureKioskModeCommand.DEFAULT_HEARTBEAT_PERIOD;
        scanErrorSetting = ConfigureKioskModeCommand.ScanErrorSettings.NO_CHANGE;
    }

    /**
     * ConfigureKioskModeResponse is the response after a Tappy has been configured.
     *
     * @param pollingSetting The polling mode setting. {@see ConfigureKioskModeCommand.PollingSettings}
     * @param ndefSetting The NDEF handling setting. {@see ConfigureKioskModeCommand.NdefSettings}
     * @param heartbeatPeriod The maximum period between heartbeats before the Tappy should
     *                        reset the connection. Maximum of 255 and a minimum value of 0.
     * @param scanErrorSetting The setting for how polling errors are handled {@see ConfigureKioskModeCommand.ScanErrorSettings}
     *
     * @throws IllegalArgumentException when a heartbeat greater than 255 is specified
     */
    public ConfigureKioskModeResponse(byte pollingSetting,
                                     byte ndefSetting,
                                     int heartbeatPeriod,
                                     byte scanErrorSetting) {
        this.pollingSetting = pollingSetting;
        this.ndefSetting = ndefSetting;
        if (heartbeatPeriod > 255 || heartbeatPeriod < 0) {
            throw new IllegalArgumentException("the heartbeat period must be between 0 and 255s, inclusive");
        }
        this.heartbeatPeriod = heartbeatPeriod;
        this.scanErrorSetting = scanErrorSetting;
    }

    /**
     * Get the polling mode, {@see ConfigureKioskModeCommand.PollingSettings}
     */
    public byte getPollingSetting() {
        return pollingSetting;
    }

    /**
     * Set the polling mode, {@see ConfigureKioskModeCommand.PollingSettings}
     */
    public void setPollingSetting(byte pollingSetting) {
        this.pollingSetting = pollingSetting;
    }

    /**
     * Get the NDEF mode setting, {@see ConfigureKioskModeCommand.NdefSettings}
     */
    public byte getNdefSetting() {
        return ndefSetting;
    }

    /**
     * Set the NDEF mode setting, {@see ConfigureKioskModeCommand.NdefSettings}
     */
    public void setNdefSetting(byte ndefSetting) {
        this.ndefSetting = ndefSetting;
    }

    /**
     * Get the set heartbeat period.
     */
    public int getHeartbeatPeriod() {
        return heartbeatPeriod;
    }

    /**
     * Set the maximum heartbeat period. Maximum value is 255.
     *
     * @throws IllegalArgumentException if the value specified is greater than 255
     */
    public void setHeartbeatPeriod(int heartbeatPeriod) {
        if (heartbeatPeriod > 255 || heartbeatPeriod < 0) {
            throw new IllegalArgumentException("the heartbeat period must be between 0 and 255, inclusive");
        }
        this.heartbeatPeriod = heartbeatPeriod;
    }

    /**
     * Get the scan error sending setting, {@see ConfigureKioskModeCommand.ScanErrorSettings}
     */
    public byte getScanErrorSetting() {
        return scanErrorSetting;
    }

    /**
     * Set the scan error sending setting, {@see ConfigureKioskModeCommand.ScanErrorSettings}
     */
    public void setScanErrorSetting(byte scanErrorSetting) {
        this.scanErrorSetting = scanErrorSetting;
    }

    /**
     * Retrieve the successful scan LED timeout in millseconds that was set. Note that this should only
     * be regarded as a legitimate value if {@link .didReceiveSuccessfulScanLedTimeout()}
     * returns true.
     */
    public int getSuccessfulScanLedTimeout() {
        return successfulScanLedTimeout;
    }

    /**
     * Set the successful scan LED timeout in milliseconds and marks it as having been received.
     *
     * @throws IllegalArgumentException if the timeout exceeds the maximum of 65535 milliseconds
     */
    public void setSuccessfulScanLedTimeout(int successfulScanLedTimeout) {
        if (successfulScanLedTimeout > 65535 || successfulScanLedTimeout < 0) {
            throw new IllegalArgumentException("the successful scan led timeout must be between 0 and 65535ms, inclusive");
        }
        hasSuccessfulScanLedTimeout = true;
        this.successfulScanLedTimeout = successfulScanLedTimeout;
    }

    /**
     * Mark the successful scan LED timeout as not having been received. Note that the
     * timeout will still be considered received if the failed led timeout or scan delay timeout
     * have been received.
     */
    public void unsetDidReceiveSuccessfulScanLedTimeout() {
        hasSuccessfulScanLedTimeout = false;
    }

    /**
     * Determines if the successful scan LED timeout was received. Note that this
     * will also be true if the failed scan timeout or post scan delay stagger have been set as received, not
     * just if the successful scan timeout has been set.
     */
    public boolean didReceiveSuccessfulScanLedTimeout() {
        return hasSuccessfulScanLedTimeout || hasFailedScanLedTimeout || hasPostScanDelayTimeout;
    }

    /**
     * Retrieve the failed scan LED timeout this command will set. Note that this should only
     * be regarded as a legitimate value if {@link .didReceiveFailedScanLedTimeout()}
     * returns true.
     */
    public int getFailedScanLedTimeout() {
        return failedScanLedTimeout;
    }

    /**
     * Set the failed scan LED timeout in milliseconds and marks it as having been received. Note
     * that this will have the side effect of marking the successful led timeout as being true.
     *
     * @throws IllegalArgumentException if the timeout exceeds the maximum of 65535 milliseconds
     */
    public void setFailedScanLedTimeout(int failedScanLedTimeout) {
        if (failedScanLedTimeout > 65535 || failedScanLedTimeout < 0) {
            throw new IllegalArgumentException("the failed scan led timeout must be between 0 and 65535ms, inclusive");
        }
        hasFailedScanLedTimeout = true;
        this.failedScanLedTimeout = failedScanLedTimeout;
    }

    /**
     * Mark the failed scan LED timeout as not having been received. Note that the
     * timeout will still be considered received if the scan delay timeout
     * has been received.
     */
    public void unsetDidReceiveFailedScanLedTimeout() {
        hasFailedScanLedTimeout = false;
    }

    /**
     * Determines if the failed scan LED timeout was received. Note that this
     * will also be true if the post scan delay has been received, not
     * just if the fail scan timeout has been received.
     */
    public boolean didReceiveFailedScanLedTimeout() {
        return hasFailedScanLedTimeout || hasPostScanDelayTimeout;
    }

    /**
     * Retrieve the post-scan delay timeout that was received. Note that this should only be
     * regarded as a legitimate value if {@link .didReceivePostScanDelayTimeout()}
     * returns true.
     */
    public int getPostScanDelayTimeout() {
        return postScanDelayTimeout;
    }

    /**
     * Set the post-scan delay timeout in milliseconds and marks it as having been received. Note that this has
     * the side effect of marking the failed and successful scan led timeouts as having been received
     *
     * @throws IllegalArgumentException if the timeout exceeds the maximum of 65535 milliseconds
     */
    public void setPostScanDelayTimeout(int postScanDelayTimeout) {
        if (postScanDelayTimeout > 65535 || postScanDelayTimeout < 0) {
            throw new IllegalArgumentException("the post scan delay timeout must be between 0 and 65535ms, inclusive");
        }
        hasPostScanDelayTimeout = true;
        this.postScanDelayTimeout = postScanDelayTimeout;
    }

    /**
     * Mark the post scan delay timeout as having not been received
     */
    public void unsetDidReceivePostScanDelayTimeout() {
        hasPostScanDelayTimeout = false;
    }

    /**
     * Determines if the post scan delay timeout was received.
     */
    public boolean didReceivePostScanDelayTimeout() {
        return hasPostScanDelayTimeout;
    }


    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 4)
            throw new IllegalArgumentException("Payload too short");

        this.pollingSetting = payload[0];
        this.ndefSetting = payload[1];
        this.heartbeatPeriod = payload[2]&0xff;
        this.scanErrorSetting = payload[3];

        this.failedScanLedTimeout = ConfigureKioskModeCommand.DEFAULT_FAILED_SCAN_LED_TIMEOUT;
        this.successfulScanLedTimeout = ConfigureKioskModeCommand.DEFAULT_SUCCESSFUL_SCAN_LED_TIMEOUT;
        this.postScanDelayTimeout = ConfigureKioskModeCommand.DEFAULT_POST_SCAN_DELAY_TIMEOUT;
        this.hasFailedScanLedTimeout = this.hasSuccessfulScanLedTimeout = this.hasPostScanDelayTimeout = false;

        if (payload.length >= 6) {
            this.hasSuccessfulScanLedTimeout = true;
            this.successfulScanLedTimeout = Utils.uint16ToInt(new byte[]{payload[4],payload[5]});
        }

        if (payload.length >= 8) {
            this.hasFailedScanLedTimeout = true;
            this.failedScanLedTimeout = Utils.uint16ToInt(new byte[]{payload[6],payload[7]});
        }

        if (payload.length >= 10) {
            this.hasPostScanDelayTimeout = true;
            this.postScanDelayTimeout = Utils.uint16ToInt(new byte[]{payload[8],payload[9]});
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() { ByteArrayOutputStream stream = new ByteArrayOutputStream(4);
        stream.write(pollingSetting);
        stream.write(ndefSetting);
        stream.write((byte) heartbeatPeriod);
        stream.write(scanErrorSetting);
        if (hasPostScanDelayTimeout || hasFailedScanLedTimeout || hasSuccessfulScanLedTimeout) {
            byte[] delayArr = Utils
                    .intToUint16(successfulScanLedTimeout);
            try {
                stream.write(delayArr);
            } catch (IOException ignored) {
                // this should be impossible
            }
        }

        if (hasPostScanDelayTimeout || hasFailedScanLedTimeout) {
            byte[] delayArr = Utils
                    .intToUint16(failedScanLedTimeout);
            try {
                stream.write(delayArr);
            } catch (IOException ignored) {
                // this should be impossible
            }
        }
        if (hasPostScanDelayTimeout) {
            byte[] delayArr = Utils
                    .intToUint16(postScanDelayTimeout);
            try {
                stream.write(delayArr);
            } catch (IOException ignored) {
                // this should be impossible
            }
        }

        return stream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
