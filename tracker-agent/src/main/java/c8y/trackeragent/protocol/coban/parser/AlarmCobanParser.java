package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.service.AlarmService;

import com.cumulocity.sdk.client.SDKException;

@Component
public class AlarmCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(AlarmCobanParser.class);
    private final AlarmService alarmService;
    
    @Autowired
    public AlarmCobanParser(TrackerAgent trackerAgent, AlarmService alarmService) {
        super(trackerAgent);
        this.alarmService = alarmService;
    }

    @Override
    protected boolean accept(String[] report) {
        if (report.length < 2) {
            return false;
        }
        return getAlarmType(report) != null;
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        CobanAlarmType alarmType = getAlarmType(reportCtx.getReport());
        logger.info("Process alarm {} for imei {}.", alarmType, reportCtx.getImei());
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        alarmService.createAlarm(reportCtx, alarmType, device);
        return true;
    }

    @Override
    protected String doParse(String[] report) {
        return CobanServerMessages.extractImeiValue(report[0]);
    }
    
    public CobanAlarmType getAlarmType(String[] report) {
        for (CobanAlarmType alarmType : CobanAlarmType.values()) {
            if (alarmType.accept(report)) {
                return alarmType;
            }
        }
        return null;
    }

}
