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

import java.util.Collections;
import java.util.List;

/**
 * This class represents the result of parsing a byte array into HDLC packets
 */
class HDLCParseResult {
    /**
     * Bytes that were attempted to be parsed
     */
    private final byte[] bytes;
    /**
     * The packets resolved out of the raw byte array
     */
    private final List<byte[]> packets;
    /**
     * Remaining bytes that were not part of a packet
     */
    private final byte[] remainder;

    /**
     * Construct an HDLC parse result for bytes containing some number of
     * extracted packets and the remaining bytes that were not part of a packet.
     *
     * @param bytes raw byte array that was parsed
     * @param packets resolved packets from parsing procedure
     * @param remainder remaining raw bytes that were not part of a complete packet
     */
    public HDLCParseResult(byte[] bytes, List<byte[]> packets, byte[] remainder) {
        this.bytes = bytes;
        this.packets = Collections.unmodifiableList(packets);
        this.remainder = remainder;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public List<byte[]> getPackets() {
        return packets;
    }

    public byte[] getRemainder() {
        return remainder;
    }
}
