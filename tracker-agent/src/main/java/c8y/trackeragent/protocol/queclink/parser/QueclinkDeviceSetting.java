/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.trackeragent.TrackerAgent;
import c8y.Tracking;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.tracker.Translator;

@Component
public class QueclinkDeviceSetting extends QueclinkParser {

    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceSetting.class);
    private final TrackerAgent trackerAgent;

    @Autowired
    public QueclinkDeviceSetting(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {

        return setDeviceInfo(reportCtx);
    }

    private boolean setDeviceInfo(ReportContext reportCtx) {

        getQueclinkDevice().getOrUpdateTrackerDevice(trackerAgent, reportCtx.getEntry(1), reportCtx.getImei());

        return true;

    }

}
