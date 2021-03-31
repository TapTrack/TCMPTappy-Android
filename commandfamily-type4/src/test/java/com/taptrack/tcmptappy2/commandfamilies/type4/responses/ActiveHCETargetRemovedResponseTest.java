package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActiveHCETargetRemovedResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ActiveHCETargetRemovedResponse timeoutResponse = new ActiveHCETargetRemovedResponse();
        assertEquals(timeoutResponse.getCommandCode(), 0x09);
    }
}