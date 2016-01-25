package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Restart;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;

public class RestartCommandTranslator implements Translator {
	
	private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);
	
	private final RFV16DeviceMessages deviceMessages;
	
	public RestartCommandTranslator(RFV16DeviceMessages deviceMessages) {
		this.deviceMessages = deviceMessages;
	}

	@Override
	public String translate(OperationContext operationCtx) {
		if (operationCtx.getOperation().get(Restart.class) == null) {
			return null;
		}
		logger.info("Handled operation {}.", operationCtx);
		return deviceMessages.restart(RFV16Constants.MAKER_TRACKER_AGENT, operationCtx.getImei()).asText();
	}
	
	

}
