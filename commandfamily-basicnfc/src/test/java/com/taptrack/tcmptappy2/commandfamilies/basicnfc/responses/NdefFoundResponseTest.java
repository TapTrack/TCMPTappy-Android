package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy.commandfamilies.basicnfc.BuildConfig;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.NdefFoundResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NdefFoundResponseTest {
    Random random = new Random();

    public NdefMessage createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return new NdefMessage(new NdefRecord[]{record});
    }

    private byte[] generateTestPayload(byte tagType, byte[] tagCode, NdefMessage message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(tagType);
        byteArrayOutputStream.write((byte) tagCode.length);
        try {
            byteArrayOutputStream.write(tagCode);
            byteArrayOutputStream.write(message.toByteArray());
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte tagType = TagTypes.MIFARE_DESFIRE;
        byte[] uid = new byte[10];
        random.nextBytes(uid);
        NdefMessage message = createTextRecord("Particularly long test payload in order to check for an issue that was uncovered",Locale.US,true);

        byte[] payload = generateTestPayload(tagType, uid, message);
        NdefFoundResponse response = new NdefFoundResponse();
        response.parsePayload(payload);
        assertEquals(response.getTagType(), tagType);
        assertArrayEquals(response.getTagCode(), uid);
        assertArrayEquals(response.getMessage().toByteArray(),message.toByteArray());
    }

    @Test
    public void testGetPayload() throws Exception {
        byte tagType = TagTypes.MIFARE_DESFIRE;
        byte[] uid = new byte[10];
        random.nextBytes(uid);
        NdefMessage message = createTextRecord("Test payload",Locale.US,true);

        byte[] payload = generateTestPayload(tagType, uid, message);
        NdefFoundResponse response = new NdefFoundResponse(uid,tagType,message);
        assertArrayEquals(response.getPayload(),payload);
    }

    @Test
    public void testGetCommandCode() throws Exception {
        NdefFoundResponse response = new NdefFoundResponse();
        assertEquals(response.getCommandCode(),0x02);
    }
}