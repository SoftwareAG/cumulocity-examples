package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlarmTypeDecoder {
    
    public static Collection<RFV16AlarmType> getAlarmTypes(String encoded) {
        BigInteger[] bases = new BigInteger[] { asBase(encoded, 0), asBase(encoded, 1), asBase(encoded, 2), asBase(encoded, 3) }; 
        List<RFV16AlarmType> result = new ArrayList<RFV16AlarmType>();
        for (RFV16AlarmType alarmType : RFV16AlarmType.values()) {
            BigInteger base = bases[alarmType.getByteNo()];
            if (!base.testBit(alarmType.getBitNo())) {
                result.add(alarmType);
            }
        }
        return result;
    }

    private static BigInteger asBase(String encoded, int byteNo) {
        String subStatus = encoded.substring(byteNo * 2, (byteNo + 1) * 2);
        int val = Integer.parseInt(subStatus, 16);
        return BigInteger.valueOf(val);
    }

}
