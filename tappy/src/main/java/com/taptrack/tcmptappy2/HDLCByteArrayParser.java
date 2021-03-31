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

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Engine for parsing raw bytes into an HDLCParseResult
 */
class HDLCByteArrayParser {
    private HDLCByteArrayParser() {

    }

    /**
     * Processes a raw byte array into an HDLCParseResult
     * @param bytes bytes to process
     * @return HDLC parse result
     */
    @NonNull
    static HDLCParseResult process(@NonNull byte[] bytes) {
        if(bytes.length > 0) {
            List<byte[]> packets = new ArrayList<>(bytes.length / 20);
            ByteArrayOutputStream commandStream = new ByteArrayOutputStream();
            for(int i = 0; i < bytes.length; i++) {
                commandStream.write(bytes[i]);
                if(bytes[i] == (byte) 0x7E) {
                    byte[] previousCommand = commandStream.toByteArray();
                    if(previousCommand.length != 0)
                        packets.add(previousCommand);

                    commandStream.reset();
                    commandStream.write((byte) 0x7E);
                }
            }
            byte[] remainder = commandStream.toByteArray();
            return new HDLCParseResult(bytes,packets,remainder);
        } else {
            return new HDLCParseResult(new byte[0],new ArrayList<byte[]>(0),new byte[0]);
        }
    }


}
