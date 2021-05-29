/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.message;

import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

@Component
public class CobanServerMessages extends TrackerMessageFactory<TrackerMessage> {
    
    private static final String IMEI_PREFIX = "imei:";
    
    @Override
    public TrackerMessage msg() {
        return new TrackerMessage(TrackingProtocol.COBAN);
    }

    public TrackerMessage load() {
        return msg().appendField("LOAD");
    }
    
    public TrackerMessage on() {
        return msg().appendField("ON");
    }
    
    public TrackerMessage timeIntervalLocationRequest(String imei, String interval) {
        return msg().appendField("**").appendField(imeiMsg(imei)).appendField("C").appendField(interval);
    }
    
    public static String imeiMsg(String imei) {
        return IMEI_PREFIX + imei;
    }
    
    public static String extractImeiValue(String imeiPart) {
        return imeiPart.replaceFirst(IMEI_PREFIX, "");
    }


    

}
