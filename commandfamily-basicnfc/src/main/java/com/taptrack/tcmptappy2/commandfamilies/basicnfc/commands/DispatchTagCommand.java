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

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.NdefFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagFoundResponse;

/**
 * Tell the Tappy to report a single tag it encounters. If the
 * tag contains NDEF data, the Tappy will read that data and respond with
 * a {@link NdefFoundResponse}
 * while tags with no NDEF content will instead be reported using
 * {@link TagFoundResponse}
 *
 * Note that this command is only present in Tappies with firmware that supports
 * version 1.4 of the BasicNfc command family
 */
public class DispatchTagCommand extends AbstractDispatchTagCommand {
    public static final byte COMMAND_CODE = (byte)0x0F;

    public DispatchTagCommand() {
        super();
    }

    public DispatchTagCommand(byte timeout) {
        super(timeout);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
