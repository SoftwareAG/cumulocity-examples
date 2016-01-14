package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.service.AlarmService;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;

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
        return false;
    }

}
