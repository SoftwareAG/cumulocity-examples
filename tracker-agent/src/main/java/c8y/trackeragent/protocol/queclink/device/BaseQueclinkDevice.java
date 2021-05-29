/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.device;

public abstract class BaseQueclinkDevice {

    
    public abstract String configureMotionTrackingCommand(String password, boolean isActive, int intervalInSeconds, short serialNumber);
    public abstract String configureMotionTrackingCommand(String password, boolean isActive, short serialNumber);
    public abstract String configureTrackingCommand(String password, int intervalInSeconds, short serialNumber);
    public abstract String getDefaultPassword();
    
}
