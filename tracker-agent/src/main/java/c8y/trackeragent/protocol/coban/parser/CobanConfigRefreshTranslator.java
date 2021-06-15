/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

import c8y.Command;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.tracker.Translator;
import c8y.trackeragent.utils.message.TrackerMessage;

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
        Command command = operation.get(Command.class);
        if (command != null) {
            TrackerDevice cobanTrackerDevice = getTrackerDevice(operationCtx.getImei());
            cobanTrackerDevice.setOperationSuccessful(operation);
            return command.getText();
        }

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
