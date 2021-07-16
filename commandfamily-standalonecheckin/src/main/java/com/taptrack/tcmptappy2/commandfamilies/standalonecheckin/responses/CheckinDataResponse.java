package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.MicrochipRtcFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CheckinDataResponse extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x01;
    protected byte[][] uids;
    protected int[] timestamp;
    protected int size;

    public CheckinDataResponse() {
        uids = new byte[1][7];
        timestamp = new int[1];
    }

    public CheckinDataResponse (byte[] payload) throws MalformedPayloadException {
        parsePayload(payload);
    }

//    public CheckinDataResponse(byte[] uid, int year, int month, int day, int hour, int minute) {
//        this.uid = uid;
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        calendar.set(year, month, day, hour, minute, 0);
//        timestamp = (int) (calendar.getTimeInMillis() / 1000L);
//    }

    /**
     * Retrieve the UID for the checkin data response
     * @return
     */
    public byte[][] getUids() {
        return uids;
    }

    /**
     * Set the UID for a checkin data response
     * @param uid
     */
    public void setUid(byte[][] uid) {
        this.uids = uid;
    }

    /**
     * Retrieves the 32-bit unix timestamp corresponding to this checkin
     *
     * Note that the Tappy only stores timestamps with minute-level accuracy, so
     * this timestamp may be up to a minute off of when the checkin actually occured
     * @return
     */
    public int[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int[] timestamp) {
        this.timestamp = timestamp;
    }

    public int getSize() { return size; }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length%12 != 0 || payload.length == 0)
            throw new MalformedPayloadException("Payload is the incorrect length");

        size = payload.length;
        int index = 0;
        uids = new byte[size/12][7];
        timestamp = new int[size/12];
        for(int i = 0; i < size; i+=12){
            byte[] uid = Arrays.copyOfRange(payload, i, i + 7);
            uids[index][0] = uid[0];
            uids[index][1] = uid[1];
            uids[index][2] = uid[2];
            uids[index][3] = uid[3];
            uids[index][4] = uid[4];
            uids[index][5] = uid[5];
            uids[index][6] = uid[6];
            byte[] bcdTime = Arrays.copyOfRange(payload, i + 7, i + 12);
            MicrochipRtcFormatter formatter = new MicrochipRtcFormatter(bcdTime[0],
                    bcdTime[1],
                    bcdTime[2],
                    bcdTime[3],
                    bcdTime[4],
                    (byte) 0x00);
            timestamp[index] = formatter.getUnixTimestamp();
            index++;
        }
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
        int index = 0;
        for(int i = 0; i < size/12; i++){
            MicrochipRtcFormatter formatter = new MicrochipRtcFormatter(timestamp[index]);

            try {
                outputStream.write(uids[index]);
                outputStream.write(formatter.getMicrochipRtcFormatted(false,false));
            } catch (IOException e) {
                throw new IllegalStateException("IO Exception on output stream writing. This shouldn't be possible");
            }
            index++;
        }


        return outputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
