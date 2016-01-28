package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

@Component
public class CobanConfigRefreshTranslator extends CobanSupport implements Translator {

    private static final Logger logger = LoggerFactory.getLogger(CobanConfigRefreshTranslator.class);
    
    public static final String OPERATION_MARKER = "refreshConfiguration";

    private final CobanServerMessages serverMessages;

    @Autowired
    public CobanConfigRefreshTranslator(TrackerAgent trackerAgent, CobanServerMessages serverMessages) {
        super(trackerAgent);
        this.serverMessages = serverMessages;
    }

    @Override
    public String translate(OperationContext operationCtx) {
        logger.info("Translate operation {}.", operationCtx);
        OperationRepresentation operation = operationCtx.getOperation();
        if (operation.get(OPERATION_MARKER) == null) {
            return null;
        }
        CobanDevice device = getCobanDevice(operationCtx.getImei());
        String locationReportInterval = device.getLocationReportInterval();
        TrackerMessage msg = serverMessages.timeIntervalLocationRequest(operationCtx.getImei(), locationReportInterval);
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        operation.set(msg.asText(), OPERATION_FRAGMENT_SERVER_COMMAND);
        logger.info("Will change location reports interval to {}.", locationReportInterval);
        return msg.asText();
    }

}
