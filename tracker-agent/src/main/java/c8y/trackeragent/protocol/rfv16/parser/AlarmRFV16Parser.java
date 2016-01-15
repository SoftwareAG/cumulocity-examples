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
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;

import com.cumulocity.sdk.client.SDKException;

public class AlarmRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(AlarmRFV16Parser.class);
    
    private final AlarmService alarmService;

    public AlarmRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages, AlarmService alarmService) {
        super(trackerAgent, serverMessages);
        this.alarmService = alarmService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String status = reportCtx.getEntry(12);
        logger.debug("Read status {} as alarms for device {}", status, reportCtx.getImei());
        Collection<RFV16AlarmType> alarmTypes = getAlarmTypes(status);
        logger.debug("Read status {} as alarms {} for device {}", status, reportCtx.getImei(), alarmTypes);
        if (alarmTypes.isEmpty()) {
            return true;
        }
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        for (RFV16AlarmType alarmType : alarmTypes) {
            alarmService.createRFV16Alarm(reportCtx, alarmType, device);
        }
        return false;
    }

    Collection<RFV16AlarmType> getAlarmTypes(String status) {
        BigInteger[] bases = new BigInteger[] { asBase(status, 0), asBase(status, 1), asBase(status, 2), asBase(status, 3) }; 
        List<RFV16AlarmType> result = new ArrayList<RFV16AlarmType>();
        for (RFV16AlarmType alarmType : RFV16AlarmType.values()) {
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
