package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.TimeZone;

public class MicrochipRtcFormatter {
    protected Calendar calendar;
    {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
    }

    public MicrochipRtcFormatter(int unixTimestamp) {

        setTimestamp(unixTimestamp);
    }

    public MicrochipRtcFormatter(byte bcdYear,
                                 byte bcdMonth,
                                 byte bcdMonthDay,
                                 byte bcdHour,
                                 byte bcdMinute,
                                 byte bcdSecond) {
        setTimestamp(2000+ByteFormatConverters.bcdToInt(bcdYear),
                ByteFormatConverters.bcdToInt(bcdMonth) - 1,
                ByteFormatConverters.bcdToInt(bcdMonthDay),
                ByteFormatConverters.bcdToInt(bcdHour),
                ByteFormatConverters.bcdToInt(bcdMinute),
                ByteFormatConverters.bcdToInt(bcdSecond));
    }


    protected void setTimestamp(int unixTimestamp) {
        calendar.setTimeInMillis(1000L * unixTimestamp);
    }

    protected void setTimestamp(int year,
                             int month,
                             int day,
                             int hour,
                             int minute,
                             int second) {
        calendar.set(year, month, day, hour, minute, second);
    }

    public int getUnixTimestamp() {
        return (int) (calendar.getTimeInMillis() / 1000L);
    }

    public byte[] getMicrochipRtcFormatted(boolean includeSecond, boolean includeDayOfWeek) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(7);

        outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.YEAR) - 2000));
        outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.MONTH)+1));
        outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.DAY_OF_MONTH)));

        outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.HOUR_OF_DAY)));
        outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.MINUTE)));
        if(includeSecond) {
            outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.SECOND)));
        }

        if(includeDayOfWeek) {
            outputStream.write(ByteFormatConverters.intToBcd(calendar.get(Calendar.DAY_OF_WEEK)));
        }

        return outputStream.toByteArray();
    }

}
