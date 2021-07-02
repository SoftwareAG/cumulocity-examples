/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.device;

public class CobanDevice {
    
    private String locationReportInterval;

    public String getLocationReportInterval() {
        return locationReportInterval;
    }

    public CobanDevice setLocationReportInterval(String locationReportInterval) {
        this.locationReportInterval = locationReportInterval;
        return this;
    }

    @Override
    public String toString() {
        return String.format("CobanDevice [locationReportInterval=%s]", locationReportInterval);
    }
}
