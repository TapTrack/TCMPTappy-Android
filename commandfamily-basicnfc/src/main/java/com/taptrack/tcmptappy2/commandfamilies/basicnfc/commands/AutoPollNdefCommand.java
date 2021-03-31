/*
 * Copyright (c) 2021. Papyrus Electronics, Inc
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

package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

/**
 * Inform the Tappy to look for tag entry and exit events, and return an NDEF message if present.
 *
 * The accepted values for ScanModeIndicator are specified in
 * {@link com.taptrack.tcmptappy2.commandfamilies.basicnfc.AutoPollingConstants.ScanModes}
 *
 * A heartbeat period of zero disables the heartbeat. Note that only values
 * from 0-255 are accepted. Other values have undefined behaviour.
 *
 * By default, the Tappy will buzz when a tag enters or exists the field.
 * The {@code buzzerDisabled} parameter allows you to turn off this functionality.
 */
public class AutoPollNdefCommand extends AbstractAutoPollCommand {
    public static final byte COMMAND_CODE = (byte)0x11;

    public AutoPollNdefCommand() {
        super();
    }

    public AutoPollNdefCommand(
            byte scanModeIndicator,
            byte heartbeatPeriod,
            boolean buzzerDisabled
            ) {
        super(scanModeIndicator, heartbeatPeriod, buzzerDisabled);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
