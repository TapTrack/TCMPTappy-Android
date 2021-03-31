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

public class ActiveHCETargetDetectedResponseTest {
    Random random = new Random();

    @Test
    public void testGeneratePayloads() {
        byte[] somecmd = new byte[7];
        byte[] longercmd = new byte[10];
        byte[] shortcmd = new byte[4];
        byte[] nocmd = new byte[0];
        random.nextBytes(somecmd);
        random.nextBytes(longercmd);
        random.nextBytes(shortcmd);

        rungenerateTest(somecmd);
        rungenerateTest(longercmd);
        rungenerateTest(shortcmd);
        rungenerateTest(nocmd);
    }

    private void rungenerateTest(byte[] firstCommand) {
        ActiveHCETargetDetectedResponse response = new ActiveHCETargetDetectedResponse(firstCommand);
        byte[] payloadBytes = response.getPayload();

        byte[] desiredBytes = generatePayload(firstCommand);

        assertArrayEquals(payloadBytes, desiredBytes);
    }

    private byte[] generatePayload(byte[] firstCommand) {
        ByteArrayOutputStream desiredStream = new ByteArrayOutputStream(firstCommand.length);
        try {
            desiredStream.write(firstCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return desiredStream.toByteArray();
    }

    @Test
    public void testFromPayloads() throws Exception {
        byte[] somecmd = new byte[7];
        byte[] longercmd = new byte[10];
        byte[] shortcmd = new byte[4];
        byte[] nocmd = new byte[0];
        random.nextBytes(somecmd);
        random.nextBytes(longercmd);
        random.nextBytes(shortcmd);

        testFromPayload(shortcmd);
        testFromPayload(somecmd);
        testFromPayload(longercmd);
        testFromPayload(nocmd);
    }

    private void testFromPayload(byte[] firstCommand) {
        byte[] payload = generatePayload(firstCommand);
        try {
            ActiveHCETargetDetectedResponse response = new ActiveHCETargetDetectedResponse();
            response.parsePayload(payload);
            assertArrayEquals(
                    String.format("Comparing first commands, expected %s, received %s",
                            TestUtils.bytesToHex(firstCommand),
                            TestUtils.bytesToHex(response.getFirstCommand())),
                    response.getFirstCommand(),firstCommand);
        } catch (MalformedPayloadException e) {
            fail(String.format("Payload improper format, " +
                    "attempting to parse first command" +
                    "from payload %s",
                    TestUtils.bytesToHex(firstCommand),
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