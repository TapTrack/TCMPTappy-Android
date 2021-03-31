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

package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;
import com.taptrack.tcmptappy2.MalformedPayloadException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Configure the kiosk mode of a Tappy. Note that the Tappy's firmware
 * must support kiosk mode for this command to have any effect.
 *
 * You can use this command to change the polling mode that the
 * Tappy will use, whether or not it will check for NDEF messages,
 * the maximum heartbeat period the Tappy will accept before resetting,
 * and whether or not the Tappy will report polling errors with a TCMP
 * error response.
 *
 * Additionally, you can also optionally configure how long the Tappy keeps
 * its LEDs green after a successful scan, how long the LEDs remain red after
 * a failed scan, and how long the Tappy will wait after a scan before attempting
 * to detect new tags. Note that due to implementation details of the command,
 * if you specify the fail LED duration, the success LED duration must also be
 * transmitted, and if you adjust the post scan delay, both of the LED durations
 * will also be transmitted.
 */
public class ConfigureKioskModeCommand extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte)0x06;
    private byte pollingSetting;
    private byte ndefSetting;

    public static final int DEFAULT_HEARTBEAT_PERIOD = 15;
    private int heartbeatPeriod = DEFAULT_HEARTBEAT_PERIOD;
    private byte scanErrorSetting;

    public static final int DEFAULT_SUCCESSFUL_SCAN_LED_TIMEOUT = 50;
    private int successfulScanLedTimeout = DEFAULT_SUCCESSFUL_SCAN_LED_TIMEOUT;
    private boolean hasSuccessfulScanLedTimeout;

    public static final int DEFAULT_FAILED_SCAN_LED_TIMEOUT = 50;
    private int failedScanLedTimeout = DEFAULT_FAILED_SCAN_LED_TIMEOUT;
    private boolean hasFailedScanLedTimeout;

    public static final int DEFAULT_POST_SCAN_DELAY_TIMEOUT = 200;
    private int postScanDelayTimeout = DEFAULT_POST_SCAN_DELAY_TIMEOUT;
    private boolean hasPostScanDelayTimeout;

    public static final int DEFAULT_POST_SUCCESSFUL_SCAN_BEEP_DURATION = 50;
    private int postSuccessfulScanBeepDuration = DEFAULT_POST_SUCCESSFUL_SCAN_BEEP_DURATION;
    private boolean hasPostScanBeepDuration;

    public interface PollingSettings {
        /**
         * NO_CHANGE tells the Tappy to use whatever setting it currently has
         */
        byte NO_CHANGE = 0x00;

        /**
         * ENABLE_DUAL_POLLING causes the Tappy to poll for both Type 1 and Type 2/4
         * tags. Enabling dual-mode polling will slightly reduce scan performance,
         * so it is best left turned off if it can be.
         */
        byte ENABLE_DUAL_POLLING= 0x01;

        /**
         * DISABLE_DUAL_POLLING causes the Tappy to poll for only Type 2/4
         * tags. Enabling dual-mode polling will slightly reduce scan performance,
         * so it is best left turned off if it can be.
         */
        byte DISABLE_DUAL_POLLING = 0x02;
    }

    public interface NdefSettings {
        /**
         * NO_CHANGE tells the Tappy to use whatever setting it currently has
         */
        byte NO_CHANGE = 0x00;

        /**
         * ENABLE_NDEF_DETECTION tells the Tappy to detect NDEF-formatted tags.
         */
        byte ENABLE_NDEF_DETECTION = 0x01;

        /**
         * DISABLE_NDEF_DETECTION tells the Tappy to skip NDEF detection and simply
         * transmit the UIDs of tags it encounters.
         */
        byte DISABLE_NDEF_DETECTION = 0x02;
    }

    public interface ScanErrorSettings {
        /**
         * NO_CHANGE tells the Tappy to use whatever setting it currently has
         */
        byte NO_CHANGE = 0x00;

        /**
         * ENABLE_SCAN_ERROR_MESSAGES tells the Tappy to report any polling errors
         * that it experiences when trying to detect tags.
         */
        byte ENABLE_SCAN_ERROR_MESSAGES = 0x01;

        /**
         * DISABLE_SCAN_ERROR_MESSAGES tells the Tappy to skip reporting polling
         * errors that it experiences when trying to detect tags.
         */
        byte DISABLE_SCAN_ERROR_MESSAGES= 0x02;
    }

    public ConfigureKioskModeCommand() {
        pollingSetting = PollingSettings.NO_CHANGE;
        ndefSetting = NdefSettings.NO_CHANGE;
        heartbeatPeriod = DEFAULT_HEARTBEAT_PERIOD;
        scanErrorSetting = ScanErrorSettings.NO_CHANGE;
    }

    /**
     * ConfigureKioskModeCommand configures the parameters used by a kiosk mode capable Tappy
     * while it is operating in kiosk mode.
     *
     * @param pollingSetting The desired polling mode. {@see PollingSettings}
     * @param ndefSetting The desired NDEF handling. {@see NdefSettings}
     * @param heartbeatPeriod The maximum period between heartbeats before the Tappy should
     *                        reset the connection. Maximum of 255 and a minimum value of 0.
     * @param scanErrorSetting The desired handling of polling errors {@see ScanErrorSettings}
     *
     * @throws IllegalArgumentException when a heartbeat greater than 255 is specified
     */
    public ConfigureKioskModeCommand(byte pollingSetting,
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
     * Get the polling mode, {@see PollingSettings}
     */
    public byte getPollingSetting() {
        return pollingSetting;
    }

    /**
     * Set the polling mode, {@see PollingSettings}
     */
    public void setPollingSetting(byte pollingSetting) {
        this.pollingSetting = pollingSetting;
    }

    /**
     * Get the NDEF mode setting, {@see NdefSettings}
     */
    public byte getNdefSetting() {
        return ndefSetting;
    }

    /**
     * Set the NDEF mode setting, {@see NdefSettings}
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
     * Get the scan error sending setting, {@see ScanErrorSettings}
     */
    public byte getScanErrorSetting() {
        return scanErrorSetting;
    }

    /**
     * Set the scan error sending setting, {@see ScanErrorSettings}
     */
    public void setScanErrorSetting(byte scanErrorSetting) {
        this.scanErrorSetting = scanErrorSetting;
    }

    /**
     * Retrieve the successful scan LED timeout in millseconds that this command will set. Note that this will only
     * be included in the command sent to the Tappy if {@link .willTransmitSuccessfulScanLedTimeout()}
     * returns true.
     */
    public int getSuccessfulScanLedTimeout() {
        return successfulScanLedTimeout;
    }

    /**
     * Set the successful scan LED timeout in milliseconds and marks it to be sent.
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
     * Disable the sending of the successful scan LED timeout if it was set. Note that the
     * timeout will still be sent if the failed scan timeout or post scan delay are sent.
     */
    public void disableTransmittingSuccessfulScanLedTimeout() {
        hasSuccessfulScanLedTimeout = false;
    }

    /**
     * Determines if the successful scan LED timeout will be transmitted. Note that this
     * will also be true if the failed scan timeout or post scan delay stagger have been set, not
     * just if the successful scan timeout has been set.
     */
    public boolean willTransmitSuccessfulScanLedTimeout() {
        return hasSuccessfulScanLedTimeout || hasFailedScanLedTimeout || hasPostScanDelayTimeout;
    }

    /**
     * Retrieve the failed scan LED timeout this command will set. Note that this will only
     * be included in the command sent to the Tappy if {@link .willTransmitFailedScanLedTimeout()}
     * returns true.
     */
    public int getFailedScanLedTimeout() {
        return failedScanLedTimeout;
    }

    /**
     * Set the failed scan LED timeout in milliseconds and marks it to be sent.
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
     * Disable the sending of the failed scan LED timeout if it was set. Note that the
     * timeout will still be sent if the fail scan timeout or post scan delay are sent.
     */
    public void disableTransmittingFailedScanLedTimeout() {
        hasFailedScanLedTimeout = false;
    }

    /**
     * Determines if the failed scan LED timeout will be transmitted. Note that this
     * will also be true if the post scan delay stagger has been set, not
     * just if the fail scan timeout has been set.
     */
    public boolean willTransmitFailedScanLedTimeout() {
        return hasFailedScanLedTimeout || hasPostScanDelayTimeout;
    }

    /**
     * Retrieve the post-scan delay timeout this command will set. Note that this will only
     * be included in the command sent to the Tappy if {@link .willTransmitPostScanDelayTimeout()}
     * returns true.
     */
    public int getPostScanDelayTimeout() {
        return postScanDelayTimeout;
    }

    /**
     * Set the post-scan delay timeout in milliseconds and marks it to be sent.
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
     * Disable the sending of the post scan delay timeout if it was set.
     */
    public void disableTransmittingPostScanDelayTimeout() {
        hasPostScanDelayTimeout = false;
    }

    /**
     * Determines if the post scan delay timeout will be transmitted.
     */
    public boolean willTransmitPostScanDelayTimeout() {
        return hasPostScanDelayTimeout;
    }


    /**
     * Retrieve the post-successful scan beep duration that this command will set. Note that this will only
     * be included in the command sent to the Tappy if {@link .willTransmitPostScanBeepDuration()}
     * returns true.
     */
    public int getPostSuccessfulScanBeepDuration() {
        return postSuccessfulScanBeepDuration;
    }

    /**
     * Set the post-scan delay timeout in milliseconds and marks it to be sent.
     *
     * @throws IllegalArgumentException if the timeout exceeds the maximum of 65535 milliseconds
     * or is below 1
     */
    public void setPostSuccessfulScanBeepDuration(int postSuccessfulScanBeepDuration) {
        if (postSuccessfulScanBeepDuration > 65535 || postSuccessfulScanBeepDuration <= 1) {
            throw new IllegalArgumentException("the post scan delay timeout must be between 0 and 65535ms, inclusive");
        }
        hasPostScanBeepDuration = true;
        this.postSuccessfulScanBeepDuration = postSuccessfulScanBeepDuration;
    }

    /**
     * Disable the sending of the post scan beep duration if it was set.
     */
    public void disableTransmittingPostScanBeepDuration() {
        hasPostScanBeepDuration = false;
    }

    /**
     * Determines if the post scan beep duration will be transmitted.
     */
    public boolean willTransmitPostScanBeepDuration() {
        return hasPostScanBeepDuration;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 4)
            throw new IllegalArgumentException("Payload too short");

        this.pollingSetting = payload[0];
        this.ndefSetting = payload[1];
        this.heartbeatPeriod = payload[2]&0xff;
        this.scanErrorSetting = payload[3];

        this.failedScanLedTimeout = DEFAULT_FAILED_SCAN_LED_TIMEOUT;
        this.successfulScanLedTimeout = DEFAULT_SUCCESSFUL_SCAN_LED_TIMEOUT;
        this.postScanDelayTimeout = DEFAULT_POST_SCAN_DELAY_TIMEOUT;
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

        if (payload.length >= 12) {
            this.hasPostScanBeepDuration = true;
            this.postSuccessfulScanBeepDuration = Utils.uint16ToInt(new byte[]{payload[10], payload[11]});
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(4);
        stream.write(pollingSetting);
        stream.write(ndefSetting);
        stream.write((byte) heartbeatPeriod);
        stream.write(scanErrorSetting);
        if (hasPostScanDelayTimeout || hasFailedScanLedTimeout || hasSuccessfulScanLedTimeout) {
            byte[] delayArr = Utils.intToUint16(successfulScanLedTimeout);
            try {
                stream.write(delayArr);
            } catch (IOException ignored) {
                // this should be impossible
            }
        }

        if (hasPostScanDelayTimeout || hasFailedScanLedTimeout) {
            byte[] delayArr = Utils.intToUint16(failedScanLedTimeout);
            try {
                stream.write(delayArr);
            } catch (IOException ignored) {
                // this should be impossible
            }
        }
        if (hasPostScanDelayTimeout) {
            byte[] delayArr = Utils.intToUint16(postScanDelayTimeout);
            try {
                stream.write(delayArr);
            } catch (IOException ignored) {
                // this should be impossible
            }
        }

        if (hasPostScanBeepDuration) {
            byte[] durationArr = Utils.intToUint16(postSuccessfulScanBeepDuration);
            try {
                stream.write(durationArr);
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
