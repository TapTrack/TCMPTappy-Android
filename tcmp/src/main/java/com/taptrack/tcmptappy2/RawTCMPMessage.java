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

package com.taptrack.tcmptappy2;


public class RawTCMPMessage extends AbstractTCMPMessage {
    protected byte[] payload;
    protected byte[] commandFamily;
    protected byte commandCode;

    public RawTCMPMessage(byte[] message) throws TCMPMessageParseException {
        try {
            if(TCMPUtils.validate(message)) {
                commandFamily = new byte[]{message[3], message[4]};
                commandCode = message[5];
                int length = message.length - 8;
                payload = new byte[length];
                System.arraycopy(message,6, payload,0,length);
            }
        } catch (MessageGarbledException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new TCMPMessageParseException("Unable to parse, invalid format",e);
        }
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        this.payload = payload;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return commandCode;
    }

    @Override
    public byte[] getCommandFamily() {
        return commandFamily;
    }
}
