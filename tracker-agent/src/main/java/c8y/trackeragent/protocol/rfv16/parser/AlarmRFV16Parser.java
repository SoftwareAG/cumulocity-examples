package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;

import com.cumulocity.sdk.client.SDKException;

public class AlarmRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(AlarmRFV16Parser.class);

    public AlarmRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages) {
        super(trackerAgent, serverMessages);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String status = reportCtx.getEntry(12);
        logger.debug("Read status {} as alarms for device {}", status, reportCtx.getImei());
        Collection<AlarmType> alarmTypes = getAlarmTypes(status);
        logger.debug("Read status {} as alarms {} for device {}", status, reportCtx.getImei(), alarmTypes);
        return false;
    }

    Collection<AlarmType> getAlarmTypes(String status) {
        BigInteger[] bases = new BigInteger[] { asBase(status, 0), asBase(status, 1), asBase(status, 2), asBase(status, 3) }; 
        List<AlarmType> result = new ArrayList<AlarmType>();
        for (AlarmType alarmType : AlarmType.values()) {
            BigInteger base = bases[alarmType.getByteNo()];
            if (!base.testBit(alarmType.getBitNo())) {
                result.add(alarmType);
            }
        }
        return result;
    }

    private static BigInteger asBase(String status, int byteNo) {
        String subStatus = status.substring(byteNo * 2, (byteNo + 1) * 2);
        int val = Integer.parseInt(subStatus, 16);
        return BigInteger.valueOf(val);
    }

}
