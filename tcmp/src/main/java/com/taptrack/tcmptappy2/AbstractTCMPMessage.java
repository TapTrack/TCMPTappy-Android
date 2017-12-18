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

import android.support.annotation.NonNull;

import java.io.IOException;

public abstract class AbstractTCMPMessage implements TCMPMessage {

    @NonNull
    @Override
    public byte[] toByteArray() {
        byte[] data = getPayload();
        byte[] family = getCommandFamily();
        byte code = getCommandCode();

        int length = data.length + 5;
        byte l1 = (byte)((length >> 8) & 0xff);
        byte l2 = (byte)((length) & 0xff);
        byte lcs = (byte) ((((byte) 0xFF - (((l1 & 0xff) + (l2 & 0xff)) & 0xff)) + (0x01 & 0xff)) & 0xff);
        byte[] frame = new byte[0];
        byte[] packet = new byte[0];
        try {
            frame = TCMPUtils.concatByteArr(new byte[]{l1,l2,lcs},family,new byte[]{code},data);
            packet = TCMPUtils.concatByteArr(frame, TCMPUtils.calculateCRCBitwise(frame));
        } catch (IOException e) {
            e.printStackTrace();
            //wtf-level error if this happens
        }
        return packet;
    }

}
