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

import java.io.ByteArrayOutputStream;

/**
 * This non-instantiable class contains some utilities for working with HDLC
 * data.
 */
class HDLCUtils {
    private HDLCUtils() {

    }

    static boolean containsHdlcEndpoint(byte[] packet) {
        for(int i = 0; i < packet.length; i++) {
            if(packet[i] == 0x7E)
                return true;
        }
        return false;
    }

    static byte[] hdlcEncodePacket(byte[] packet) {
        byte[] encodedPacket = hdlcEncodeData(packet);
        byte[] resultingPacket = new byte[encodedPacket.length+2];
        resultingPacket[0] = (byte) 0x7E;
        System.arraycopy(encodedPacket,0,resultingPacket,1,encodedPacket.length);
        resultingPacket[resultingPacket.length - 1] = (byte) 0x7E;
        return resultingPacket;
    }

    private static byte[] hdlcEncodeData(byte[] data) {
        if(data == null || data.length == 0) {
            return new byte[0];
        }
        else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

            for(int i = 0; i < data.length; i++) {
                if(data[i] == (byte) 0x7E) {
                    outputStream.write(0x7D);
                    outputStream.write(0x5E);
                }
                else if(data[i] == (byte) 0x7D) {
                    outputStream.write(0x7D);
                    outputStream.write(0x5D);
                }
                else {
                    outputStream.write(data[i]);
                }
            }


            return outputStream.toByteArray();
        }
    }

    public static byte[] hdlcDecodePacket(byte[] data) {
        if(data == null || data.length == 0) {
            return new byte[0];
        }
        else if(data.length >= 2) {
            if(data[0] == (byte) 0x7E && data[data.length - 1] == (byte) 0x7E) {
                byte[] packetPayload = new byte[data.length - 2];
                System.arraycopy(data,1,packetPayload,0,data.length-2);
                return hdlcDecodeData(packetPayload);
            }
            else {
                throw new IllegalHDLCFormatException("Packet malformed");
            }
        }
        else {
            throw new IllegalHDLCFormatException("Packet malformed");
        }
    }


    private static byte[] hdlcDecodeData(byte[] data) {
        if(data == null || data.length == 0) {
            return new byte[0];
        }
        else {
            ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream(data.length);
            for(int i = 0; i < data.length; i++) {
                if(data[i] == (byte) 0x7D) {
                    if((i+1) > data.length) {
                        throw new IllegalHDLCFormatException("0x7D with no escaped character");
                    }
                    else if (data[i+1] == (byte) 0x5E) {
                        decodedBytes.write(0x7E);
                        i++;
                    }
                    else if (data[i+1] == (byte) 0x5D) {
                        decodedBytes.write(0x7D);
                        i++;
                    }
                    else {
                        throw new IllegalHDLCFormatException("0x7D escaping unrecognized character");
                    }
                }
                else {
                    decodedBytes.write(data[i]);
                }
            }
            return decodedBytes.toByteArray();
        }
    }

}
