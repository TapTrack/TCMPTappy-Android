package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Type4BDetectedResponseTest {
    Random random = new Random();

    private byte[] generatePayload(byte[] atqb, byte[] attrib) {
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(2+atqb.length+attrib.length);
        try {
            oStream.write((byte) atqb.length);
            oStream.write((byte) attrib.length);
            oStream.write(atqb);
            oStream.write(attrib);
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        return oStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte[] zeroLenArr = new byte[0];
        byte[] hugeArr1 = new byte[255];
        byte[] hugeArr2 = new byte[255];
        random.nextBytes(hugeArr1);
        random.nextBytes(hugeArr2);

        runParseTest(zeroLenArr,zeroLenArr);
        runParseTest(zeroLenArr,hugeArr1);
        runParseTest(hugeArr1,zeroLenArr);
        runParseTest(hugeArr1,hugeArr2);
    }

    public void runParseTest(byte[] atqb, byte[] attrib) {
        byte[] payload = generatePayload(atqb,attrib);
        Type4BDetectedResponse response = new Type4BDetectedResponse();
        try {
            response.parsePayload(payload);
        } catch (MalformedPayloadException e) {
            fail(e.getMessage());
        }
        assertArrayEquals(response.getAtqb(),atqb);
        assertArrayEquals(response.getAttrib(),attrib);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte[] zeroLenArr = new byte[0];
        byte[] hugeArr1 = new byte[255];
        byte[] hugeArr2 = new byte[255];
        random.nextBytes(hugeArr1);
        random.nextBytes(hugeArr2);

        runGetTest(zeroLenArr,zeroLenArr);
        runGetTest(zeroLenArr,hugeArr1);
        runGetTest(hugeArr1,zeroLenArr);
        runGetTest(hugeArr1,hugeArr2);
    }

    private void runGetTest(byte[] atqb, byte[] attrib) {
        byte[] payload = generatePayload(atqb,attrib);
        Type4BDetectedResponse response = new Type4BDetectedResponse(atqb,attrib);
        byte[] testPayload = response.getPayload();
        assertArrayEquals(payload,testPayload);
    }

    @Test
    public void testGetCommandCode() throws Exception {
        Type4BDetectedResponse response = new Type4BDetectedResponse();
        assertEquals(response.getCommandCode(),(byte)0x07);
    }
}