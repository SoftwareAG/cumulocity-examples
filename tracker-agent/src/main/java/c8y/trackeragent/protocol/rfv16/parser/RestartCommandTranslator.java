package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Restart;
import c8y.trackeragent.ConnectionContext;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;

public class RestartCommandTranslator implements Translator {

    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);

    private final RFV16ServerMessages serverMessages;

    public RestartCommandTranslator(RFV16ServerMessages serverMessages) {
	this.serverMessages = serverMessages;
    }

    @Override
    public String translate(OperationContext operationCtx) {
	if (operationCtx.getOperation().get(Restart.class) == null) {
	    return null;
	}
	logger.info("Handled operation {}.", operationCtx);
	String maker = getMaker(operationCtx);
	if (maker == null) {
	    return null;
	} else {
	    return serverMessages.restart(maker, operationCtx.getImei()).asText();
	}
    }

    protected String getMaker(ConnectionContext connectionContext) {
	Object connectionParam = connectionContext.getConnectionParam(RFV16Constants.CONNECTION_PARAM_MAKER);
	if (connectionParam == null) {
	    logger.warn("There are no maker param in connection {}.", connectionContext);
	    return "";
	}
	return connectionParam.toString();
    }

}
