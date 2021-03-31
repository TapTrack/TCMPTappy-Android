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

package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

/**
 * Inform the Tappy to scan for tags. If the Tappy detects a tag, it will
 * stop scanning.
 *
 * A timeout of zero corresponds to indefinite scanning.
 */
public class ScanTagCommand extends AbstractPollingCommand {
    public static final byte COMMAND_CODE = (byte)0x02;

    public ScanTagCommand() {
        super();
    }

    public ScanTagCommand(byte timeout, byte pollingMode) {
        super(timeout, pollingMode);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
