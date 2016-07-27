package c8y.trackeragent.protocol.rfv16.parser;

import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_POSITION_MONITORING;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.CONNECTION_PARAM_CONTROL_COMMANDS_SENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.tracker.Parser;

/**
 * listen to response on D1 server command
 *
 */
@Component
public class ConfirmPositionMonitoringCommandRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(ConfirmPositionMonitoringCommandRFV16Parser.class);
    
    @Autowired
    public ConfirmPositionMonitoringCommandRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages, AlarmService alarmService) {
        super(trackerAgent, serverMessages, alarmService);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isConfirmPositionMonitorinCommand(reportCtx)) {
            return false;
        }
        logger.info("Received response on {} command: {}", COMMAND_POSITION_MONITORING, reportCtx);
        reportCtx.setConnectionParam(CONNECTION_PARAM_CONTROL_COMMANDS_SENT, true);
        return true;
    }

    private boolean isConfirmPositionMonitorinCommand(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_V4.equalsIgnoreCase(reportCtx.getEntry(2))
                && RFV16Constants.COMMAND_POSITION_MONITORING.equalsIgnoreCase(reportCtx.getEntry(3));
    }

}
