/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.rfv16.parser;

import static c8y.trackeragent.utils.LocationEventBuilder.aLocationEvent;

import java.math.BigDecimal;
import java.util.Collection;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Strings;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.tracker.Parser;
import c8y.trackeragent.utils.LocationEventBuilder;

/**
 * listen to HEARTBEAT (LINK) message
 *
 */
@Component
public class HeartbeatRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(HeartbeatRFV16Parser.class);
    
    private final MeasurementService measurementService;

    @Autowired
    public HeartbeatRFV16Parser(TrackerAgent trackerAgent, 
            RFV16ServerMessages serverMessages, 
            AlarmService alarmService,
            MeasurementService measurementService
            ) {
        super(trackerAgent, serverMessages, alarmService);
        this.measurementService = measurementService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isHeartbeat(reportCtx)) {
            return false;
        }        
        TrackerDevice device = getTrackerDevice(reportCtx.getImei());
        ping(reportCtx, device);
        String status = reportCtx.getEntry(10);
        if (Strings.isNullOrEmpty(status)) {
            logger.debug("Invalid heartbeat message format (empty status) {}", reportCtx);
            return false;
        }
        logger.debug("Read status {} as alarms for device {}", status, reportCtx.getImei());
        Collection<AlarmRepresentation> alarms = createAlarms(reportCtx, device, status);
		if (!alarms.isEmpty()) {
        	sendAlarmsWithLastPosition(reportCtx, device, alarms);
        }
        BigDecimal batteryLevel = getBatteryPercentageLevel(reportCtx);
        if (batteryLevel != null) {
            measurementService.createPercentageBatteryLevelMeasurement(batteryLevel, device, new DateTime());
        }
        BigDecimal gsmLevel = getGSMPercentageLevel(reportCtx);
        if (gsmLevel != null) {
            measurementService.createGSMLevelMeasurement(gsmLevel, device, new DateTime());
        }
        Integer satellites = getGPSSatellites(reportCtx);
        if (satellites != null) {
            BigDecimal quality = resolveQualityLevel(satellites);
            measurementService.createGpsQualityMeasurement(satellites.intValue(), quality, device, new DateTime());
        }
        
        return true;
    }

    private BigDecimal resolveQualityLevel(Integer satellites) {
        if (satellites < 2) {
            return new BigDecimal(0);
        } else if (satellites > 11) {
            return new BigDecimal(100);
        } else {
            return new BigDecimal((satellites - 2) * 10);
        }
    }

    private void sendAlarmsWithLastPosition(ReportContext reportCtx, TrackerDevice device, Collection<AlarmRepresentation> alarms) {
        Position lastPosition = device.getLastPosition();
        if (lastPosition == null) {
            return;
        }
        LocationEventBuilder locationEvent = aLocationEvent()
        		.withSourceId(device.getGId())
        		.withPosition(lastPosition)
        		.withAlarms(alarms);
        device.setPosition(locationEvent.build());
		
	}

	private boolean isHeartbeat(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_LINK.equalsIgnoreCase(reportCtx.getEntry(2));
    }

    private void ping(ReportContext reportCtx, TrackerDevice device) {
        logger.debug("Heartbeat for imei {}.", reportCtx.getImei());
        try {
            device.ping();
        } catch (Exception ex) {
            logger.error("Error processing heartbeat on imei " +  reportCtx.getImei(), ex);
        }
    }
    
    private BigDecimal getBatteryPercentageLevel(ReportContext reportCtx) {
        return reportCtx.getEntryAsNumber(6);
    }
    
    private BigDecimal getGSMPercentageLevel(ReportContext reportCtx) {
        return reportCtx.getEntryAsNumber(4);
    }
    
    private Integer getGPSSatellites(ReportContext reportCtx) {
        return reportCtx.getEntryAsInt(5);
    }



}
