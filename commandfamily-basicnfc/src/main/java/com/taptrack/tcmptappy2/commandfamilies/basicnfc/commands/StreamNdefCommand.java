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
 * Tell the Tappy to continuously report NDEF-formatted tags that it detects
 */
public class StreamNdefCommand extends AbstractPollingCommand {
    public static final byte COMMAND_CODE = (byte)0x03;

    public StreamNdefCommand() {
        super();
    }

    public StreamNdefCommand(byte timeout, byte pollingMode) {
        super(timeout, pollingMode);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
