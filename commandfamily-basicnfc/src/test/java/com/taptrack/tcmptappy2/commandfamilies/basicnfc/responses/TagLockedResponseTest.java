package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagLockedResponse;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TagLockedResponseTest {
    protected Random random = new Random();

    protected static byte[] generatePayload(byte tagType, byte[] tagCode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(tagType);
        outputStream.write(tagCode.length);
        try {
            outputStream.write(tagCode);
        } catch (IOException ignored) {
        }
        return outputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte[] tagCode = new byte[7];
        random.nextBytes(tagCode);
        byte tagType = 0x05;

        byte[] payload = generatePayload(tagType,tagCode);
        TagLockedResponse response = new TagLockedResponse();
        response.parsePayload(payload);
        assertEquals(tagType,response.getTagType());
        assertArrayEquals(tagCode,response.getTagCode());
    }

    @Test
    public void testGetPayload() throws Exception {
        byte[] tagCode = new byte[10];
        random.nextBytes(tagCode);
        byte tagType = 0x02;

        byte[] payload = generatePayload(tagType,tagCode);
        TagLockedResponse response = new TagLockedResponse(tagCode,tagType);
        assertArrayEquals(payload,response.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        TagLockedResponse response = new TagLockedResponse();
        assertEquals(response.getCommandCode(),(byte)0x06);
    }
}