package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AutoPollingConstants;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AutoPollTagEnteredResponseTest {
    Random random = new Random();

    private byte[] generateTestPayload(byte tagType, byte[] metadata) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(tagType);
        try {
            byteArrayOutputStream.write(metadata);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte tagType = AutoPollingConstants.ResponseTagTypes.TYPE_4A;
        byte[] metadata = new byte[45];
        random.nextBytes(metadata);
        byte[] payload = generateTestPayload(tagType,metadata);

        AutoPollTagEnteredResponse response = new AutoPollTagEnteredResponse();
        response.parsePayload(payload);
        assertEquals(tagType, response.getDetectedTagType());
        assertArrayEquals(metadata,response.getTagMetadata());
    }

    @Test
    public void testGetPayload() throws Exception {
        byte tagType = AutoPollingConstants.ResponseTagTypes.TYPE_2;
        byte[] metadata = new byte[1];
        random.nextBytes(metadata);
        byte[] payload = generateTestPayload(tagType,metadata);

        AutoPollTagEnteredResponse response = new AutoPollTagEnteredResponse(tagType,metadata);
        assertArrayEquals(payload,response.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        AutoPollTagEnteredResponse response = new AutoPollTagEnteredResponse();
        assertEquals(response.getCommandCode(),0x0C);
    }
}