package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class CobanConfigRefreshTranslator extends CobanSupport implements Translator {

    private static final Logger logger = LoggerFactory.getLogger(CobanConfigRefreshTranslator.class);
    
    public static final String OPERATION_MARKER = "refreshConfiguration";

    private final CobanServerMessages serverMessages;


    public CobanConfigRefreshTranslator(TrackerAgent trackerAgent, CobanServerMessages serverMessages) {
        super(trackerAgent);
        this.serverMessages = serverMessages;
    }

    @Override
    public String translate(OperationContext operationCtx) {
        logger.debug("Translate operation {}.", operationCtx);
        OperationRepresentation operation = operationCtx.getOperation();
        if (operation.get(OPERATION_MARKER) == null) {
            return null;
        }
        CobanDevice device = getCobanDevice(operationCtx.getImei());
        String locationReportInterval = device.getLocationReportInterval();
        TrackerMessage msg = serverMessages.timeIntervalLocationRequest(operationCtx.getImei(), locationReportInterval);
        logger.info("Send message to device: {}.", msg);
        return msg.asText();
    }

}
