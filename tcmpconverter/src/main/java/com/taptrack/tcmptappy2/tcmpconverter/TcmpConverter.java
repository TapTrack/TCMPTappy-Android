package com.taptrack.tcmptappy2.tcmpconverter;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy2.RawTCMPMessage;
import com.taptrack.tcmptappy2.TCMPMessageParseException;

public class TcmpConverter {
    public static com.taptrack.tcmptappy2.TCMPMessage toVersionTwo(TCMPMessage msg) {
        try {
            return new RawTCMPMessage(msg.toByteArray());
        } catch (TCMPMessageParseException e) {
            throw new IllegalArgumentException("Invalid TCMP message format: this should not be " +
                    "possible given that formats are the same",e);
        }
    }

    public static TCMPMessage fromVersionTwo(com.taptrack.tcmptappy2.TCMPMessage msg) {
        try {
            return new com.taptrack.tcmptappy.tcmp.RawTCMPMessage(msg.toByteArray());
        } catch (com.taptrack.tcmptappy.tcmp.TCMPMessageParseException e) {
            throw new IllegalArgumentException("Invalid TCMP message format: this should not be " +
                    "possible given that formats are the same",e);
        }
    }
}
