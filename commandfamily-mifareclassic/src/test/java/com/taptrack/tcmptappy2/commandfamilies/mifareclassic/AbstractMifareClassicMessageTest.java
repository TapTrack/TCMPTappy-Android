package com.taptrack.tcmptappy2.commandfamilies.mifareclassic;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class AbstractMifareClassicMessageTest {

    @Test
    public void testGetCommandFamily() throws Exception {
        AbstractMifareClassicMessage testMessage = new AbstractMifareClassicMessage() {
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

        assertArrayEquals(testMessage.getCommandFamily(),new byte[]{0x00,0x03});
    }
}
