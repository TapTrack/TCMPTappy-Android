package com.taptrack.tcmptappy2.commandfamilies.basicnfc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TlvParser {
    public static class Tlv {
        private byte type;
        private byte[] value;

        public Tlv(byte type, byte[] value) {
            this.type = type;
            this.value = value;
        }

        public byte getType() {
            return type;
        }

        public byte[] getValue() {
            return value;
        }
    }

    public static List<Tlv> parseTlvsFromBinary(byte[] data) {
        if(data.length < 2) {
            return new ArrayList<>(0);
        }

        int currentIdx = 0;
        List<Tlv> tlvs = new ArrayList<>(1);
        while((currentIdx+2) <= data.length) {
            byte type = data[currentIdx];
            int length = data[currentIdx+1] & 0xff;

            if(length >= 255) {
                throw new IllegalArgumentException("This tlv system doesn't support value lengths of 255 bytes or longer");
            }

            if(data.length < (currentIdx+length+2)) {
                throw new IllegalArgumentException("Data too short to contain value specified in length");
            }

            if(length > 0) {
                int dataStart = currentIdx + 2;
                int dataEnd = dataStart + length;
                tlvs.add(new Tlv(type, Arrays.copyOfRange(data, dataStart, dataEnd)));
                currentIdx = dataEnd;
            } else {
                tlvs.add(new Tlv(type,new byte[0]));
                currentIdx = currentIdx+2;
            }
        }

        return tlvs;
    }

    public static byte[] composeBinaryFromTlvs(List<Tlv> data) {
        if(data.size() == 0) {
            return new byte[0];
        }

        ByteArrayOutputStream boStream = new ByteArrayOutputStream(data.size()*(2+data.get(0).getValue().length));
        for(int i = 0; i < data.size(); i++) {
            Tlv tlv = data.get(i);
            if(tlv.value.length >= 255) {
                throw new IllegalArgumentException("This tlv system doesn't support value lengths of 255 bytes or longer");
            }
            boStream.write(tlv.getType());
            boStream.write(tlv.getValue().length);
            try {
                boStream.write(tlv.getValue());
            } catch (IOException ignored) {}
        }


        return boStream.toByteArray();
    }
}
