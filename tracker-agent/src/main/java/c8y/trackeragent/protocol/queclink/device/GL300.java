/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.device;

public class GL300 extends BaseQueclinkDevice {

    public static final String motionTemplate = "AT+GTCFG=%s,,,,,,,,,,%d,%d,,,,,,,,,,%04x$";
    public static final String motionWithIntervalTemplate = "AT+GTFRI=%s,1,,,,,,%d,%d,,,,,,,,,,,,%04x$";
    public static final String nonMovementIntervalTemplate = "AT+GTNMD=%s,%s,,,,%d,%d,,,,,,,,%04x$";
    
    /**
     * Events to set: Power on/off, external power on/off, battery low are
     * always on. Device motion is added depending on configuration from
     * platform.
     */
    public static final int MOTION_OFF = 1 + 2 + 4 + 8 + 32;
    public static final int MOTION_ON = 1 + 2 + 4 + 8 + 32 + 256;
    
    /**
     * Bitmask for non-movement report interval:
     * Report message when it detects non movement: 2
     * Report message when it detects movement: 4 
     * Change the fix interval and send interval of FRI to <rest fix interval> and <rest send interval> when it detects non-movement: 8
     */
    public static final String BITMASK_MODENOMOTION = "E"; // 2 + 4 + 8
    
    @Override
    public String configureMotionTrackingCommand(String password, boolean isActive, int intervalInSeconds, short serialNumber) {
        
        String device_command =  String.format(motionWithIntervalTemplate, 
                password, intervalInSeconds, 
                intervalInSeconds, serialNumber);
        
        device_command += configureMotionTrackingCommand(password, isActive, serialNumber);
        
        return device_command;
    }

    @Override
    public String configureMotionTrackingCommand(String password, boolean isActive, short serialNumber) {

        return String.format(motionTemplate, 
                password, isActive ? MOTION_ON : MOTION_OFF, 
                        isActive ? 1 : 0, serialNumber);
    }

    @Override
    public String configureTrackingCommand(String password, int intervalInSeconds, short serialNumber) {

        return String.format(nonMovementIntervalTemplate, 
                password, BITMASK_MODENOMOTION, intervalInSeconds, 
                intervalInSeconds, serialNumber);
    }

    @Override
    public String getDefaultPassword() {
        return "gl300";
    }

}
