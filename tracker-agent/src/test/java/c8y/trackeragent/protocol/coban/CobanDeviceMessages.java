/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban;

import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.coban.parser.CobanAlarmType;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.SignedLocation;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

public class CobanDeviceMessages extends TrackerMessageFactory<TrackerMessage> {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanDeviceMessages.class);
    
    private static final Integer DEFAULT_SPEED = 120;
    private static final Position DEFAULT_POSITION = Positions.ZERO;
    
    private static final String LOGON = "##,imei:%s,A;";
    private static final String HEARTBEAT = "%s;";
    private static final String POSITION_UPDATE = "imei:%s,tracker,0809231929,,%s,055403.000,A,%s,%s,%s,%s,%s,,;";
    private static final String ALARM = "imei:%s,%s,0809231929,,F,055403.000,A,,,,,,100,;";
    private static final String OVERSPEED_ALARM = "imei:%s,%s,0809231929,,F,055403.000,A,,,,,%s,,;";
    
    @Override
    public TrackerMessage msg() {
        return new TrackerMessage(TrackingProtocol.COBAN);
    }


    public TrackerMessage logon(final String imei) {
        String msg = formatMessage(LOGON, imei);
        return msg().fromText(msg);
    }
    
    public TrackerMessage heartbeat(String imei) {
        String msg = formatMessage(HEARTBEAT, imei);
        return msg().fromText(msg);
    }
    
    private TrackerMessage positionUpdate(String imei, Position position, String gpsStatus, Integer speed) {
        SignedLocation lat = latitude().withValue(position.getLat());
        SignedLocation lng = longitude().withValue(position.getLng());
        // formatter:off
        String text = formatMessage(POSITION_UPDATE, 
                imei, 
                gpsStatus, 
                lat.getAbsValue(), 
                lat.getSymbol(), 
                lng.getAbsValue(), 
                lng.getSymbol(), 
                speed);
        // formatter:on
        return msg().fromText(text);
    }
    
    public TrackerMessage positionUpdate(String imei, Position position) {
        return positionUpdate(imei, position, CobanConstants.GPS_OK, DEFAULT_SPEED);
    }
    
    public TrackerMessage positionUpdate(String imei, Integer speed) {
        return positionUpdate(imei, DEFAULT_POSITION, CobanConstants.GPS_OK, speed);
    }
    
    public TrackerMessage positionUpdateNoGPS(String imei) {
        return positionUpdate(imei, DEFAULT_POSITION, CobanConstants.GPS_KO, DEFAULT_SPEED);
    }
    
    public TrackerMessage alarm(String imei, CobanAlarmType type) {
        String msg = formatMessage(ALARM, imei, type.asCobanType());
        return msg().fromText(msg);
    }
    
    public TrackerMessage overSpeedAlarm(String imei, int speed) {
        String msg = formatMessage(OVERSPEED_ALARM, imei, CobanAlarmType.OVERSPEED.asCobanType(), speed);
        return msg().fromText(msg);
    }

    private static String formatMessage(String format, Object... params) {
        String msg = format(format, params);
        logger.info("Message prepared:{}", msg);
        return msg;
    }

}
