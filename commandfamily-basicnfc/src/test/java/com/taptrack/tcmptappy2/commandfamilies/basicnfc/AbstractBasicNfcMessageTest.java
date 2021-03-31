package com.taptrack.tcmptappy2.commandfamilies.basicnfc;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class AbstractBasicNfcMessageTest {

    @Test
    public void testGetCommandFamily() throws Exception {
        TCMPMessage message = new AbstractBasicNfcMessage() {
            @Override
            public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {

            }

            @NonNull
            @Override
            public byte[] getPayload() {
                return new byte[0];
            }

            @Override
            public byte getCommandCode() {
                return 0;
            }
        };

        assertArrayEquals(message.getCommandFamily(),new byte[]{0x00,0x01});
    }
}
