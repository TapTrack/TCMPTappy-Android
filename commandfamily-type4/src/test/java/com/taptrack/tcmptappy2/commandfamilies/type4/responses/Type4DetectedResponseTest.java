package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.TestUtils;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Type4DetectedResponseTest {
    Random random = new Random();

    @Test
    public void testGeneratePayloads() {
        byte[] uid7 = new byte[7];
        byte[] uid10 = new byte[10];
        byte[] uid4 = new byte[4];
        byte[] ats = new byte[3];
        byte[] noats = new byte[0];
        random.nextBytes(uid7);
        random.nextBytes(uid10);
        random.nextBytes(uid4);
        random.nextBytes(ats);

        rungenerateTest(uid4, ats);
        rungenerateTest(uid4, noats);

        rungenerateTest(uid7,ats);
        rungenerateTest(uid7,noats);

        rungenerateTest(uid10,ats);
        rungenerateTest(uid10,noats);
    }

    private void rungenerateTest(byte[] uid, byte[] ats) {
        Type4DetectedResponse response = new Type4DetectedResponse(uid,ats);
        byte[] payloadBytes = response.getPayload();

        byte[] desiredBytes = generatePayload(uid,ats);

        assertArrayEquals(payloadBytes, desiredBytes);
    }

    private byte[] generatePayload(byte[] uid, byte[] ats) {
        ByteArrayOutputStream desiredStream = new ByteArrayOutputStream(uid.length+ats.length+1);
        desiredStream.write((byte)uid.length);
        try {
            desiredStream.write(uid);
            desiredStream.write(ats);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return desiredStream.toByteArray();
    }

    @Test
    public void testFromPayloads() throws Exception {
        byte[] uid7 = new byte[7];
        byte[] uid10 = new byte[10];
        byte[] uid4 = new byte[4];
        byte[] ats = new byte[3];
        byte[] noats = new byte[0];
        random.nextBytes(uid7);
        random.nextBytes(uid10);
        random.nextBytes(uid4);
        random.nextBytes(ats);

        //with ats
        testFromPayload(uid4, ats);
        testFromPayload(uid7, ats);
        testFromPayload(uid10, ats);
        //without ats
        testFromPayload(uid4, noats);
        testFromPayload(uid7,noats);
        testFromPayload(uid10,noats);
    }

    private void testFromPayload(byte[] uid, byte[] ats) {
        byte[] payload = generatePayload(uid,ats);
        try {
            Type4DetectedResponse response = new Type4DetectedResponse();
            response.parsePayload(payload);
            assertArrayEquals(
                    String.format("Comparing UIDs, expected %s, received %s",
                            TestUtils.bytesToHex(uid),
                            TestUtils.bytesToHex(response.getUid())),
                    response.getUid(),uid);
            assertArrayEquals(
                    String.format("Comparing ATSs, expected %s, received %s",
                            TestUtils.bytesToHex(ats),
                            TestUtils.bytesToHex(response.getAts())),
                    response.getAts(), ats);
        } catch (MalformedPayloadException e) {
            fail(String.format("Payload improper format, " +
                    "attempting to parse UID: %s, ATS %s " +
                    "from payload %s",
                    TestUtils.bytesToHex(uid),
                    TestUtils.bytesToHex(ats),
                    TestUtils.bytesToHex(payload)));
        }
    }



    @Test
    public void testGetCommandCode() throws Exception {
        Type4DetectedResponse response = new Type4DetectedResponse();
        assertEquals(response.getCommandCode(),0x01);
    }

    @Test
    public void testGetCommandFamily() throws Exception {
        Type4DetectedResponse response = new Type4DetectedResponse();
        assertArrayEquals(response.getCommandFamily(), new byte[]{0x00, 0x04});
    }
}