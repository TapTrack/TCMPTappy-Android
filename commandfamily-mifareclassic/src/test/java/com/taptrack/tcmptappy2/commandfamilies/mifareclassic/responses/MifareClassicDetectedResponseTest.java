package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MifareClassicDetectedResponseTest {
    Random rand = new Random();

    private byte[] generateUid(boolean sevenByte) {
        byte[] uid;
        if(sevenByte) {
            uid = new byte[7];
        }
        else {
            uid = new byte[4];
        }

        rand.nextBytes(uid);
        return uid;
    }

    private byte[] getTestPayload(byte type, byte[] uid) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outStream.write(type);
        try {
            outStream.write(uid);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return outStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte[] shortUid = generateUid(false);
        byte[] longUid = generateUid(true);

        byte type = MifareClassicDetectedResponse.ClassicType.CLASSIC_1K;
        byte[] longPayload = getTestPayload(type,longUid);
        byte[] shortPayload = getTestPayload(type,shortUid);

        MifareClassicDetectedResponse longResponse = new MifareClassicDetectedResponse();
        longResponse.parsePayload(longPayload);

        assertArrayEquals(longResponse.getUid(), longUid);
        assertEquals(longResponse.getType(),type);

        MifareClassicDetectedResponse shortResponse = new MifareClassicDetectedResponse();
        shortResponse.parsePayload(shortPayload);

        assertArrayEquals(shortResponse.getUid(), shortUid);
        assertEquals(shortResponse.getType(),type);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte[] shortUid = generateUid(false);
        byte[] longUid = generateUid(true);

        byte type = MifareClassicDetectedResponse.ClassicType.CLASSIC_1K;
        byte[] longPayload = getTestPayload(type,longUid);
        byte[] shortPayload = getTestPayload(type,shortUid);

        MifareClassicDetectedResponse shortResponse = new MifareClassicDetectedResponse(type,shortUid);
        assertArrayEquals(shortResponse.getPayload(),shortPayload);

        MifareClassicDetectedResponse longResponse = new MifareClassicDetectedResponse(type,longUid);
        assertArrayEquals(longResponse.getPayload(),longPayload);
    }

    @Test
    public void testGetCommandCode() throws Exception {
        MifareClassicDetectedResponse response = new MifareClassicDetectedResponse();
        assertEquals(response.getCommandCode(),0x02);
    }

}