/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.parser;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.protocol.queclink.device.QueclinkReport;
import c8y.trackeragent.tracker.Parser;

public abstract class QueclinkParser implements Parser, QueclinkFragment {
    
    protected static final String PASSWORD = "gl200";
    private QueclinkDevice queclinkDevice = new QueclinkDevice();
    protected QueclinkReport queclinkReport = new QueclinkReport();
    
    @Override
    public String parse(String[] report) throws SDKException {
        return report.length > 2 ? report[2] : null;
    }
    
    public QueclinkDevice getQueclinkDevice() {
        return queclinkDevice;
    }

}
