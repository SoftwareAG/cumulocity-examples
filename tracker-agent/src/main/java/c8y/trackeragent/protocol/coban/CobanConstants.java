/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban;

public class CobanConstants {

    public static final Integer DEFAULT_LOCATION_REPORT_INTERVAL = 180;
    
    public static final String DEVICE_CONFIG_FRAGMENT = "coban_config";
    public static final String DEVICE_CONFIG_KEY_LOCATION_REPORT_TIME_INTERVAL = "locationReportTimeInterval";
    
    public static final String GPS_OK = "F"; 
    public static final String GPS_KO = "L"; 

}
