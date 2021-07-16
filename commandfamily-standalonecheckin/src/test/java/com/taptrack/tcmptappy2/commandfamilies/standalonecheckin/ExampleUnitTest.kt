package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin

import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.CheckinDataResponse
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkinDataPayload() {
        val payload = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 21.toByte(), 0x07, 0x05, 0x09, 28.toByte())
        val command = CheckinDataResponse(payload)
        Assert.assertArrayEquals(command.uids[0], byteArrayOf(0x00,0x00,0x00,0x00,0x00,0x00,0x00))
    }
}