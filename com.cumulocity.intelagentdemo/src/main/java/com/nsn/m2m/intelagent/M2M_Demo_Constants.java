/*
 * Copyright 2012 Nokia Siemens Networks 
 */

package com.nsn.m2m.intelagent;

public interface M2M_Demo_Constants
{
	public static final String INTEL_AGENT_TYPE = "com.nsn.m2m.IntelAgent" ;
	public static final String INTEL_AGENT_ETH0_MAC_PROP = "eth0.mac.address" ;
	public static final String INTEL_AGENT_DEFAULT_NAME = "Intel Smart Agent" ;
	public static final String INTEL_AGENT_EXTERNAL_ID = "IntelSmartAgent" ;
	public static final String INTEL_AGENT_EXTERNAL_TYPE = "com.cumulocity.intelagent.mac_id" ;
	public static final String FRI_ETH0_MAC_FILE = "/sys/class/net/eth0/address" ;
	public static final String FRI_ETH0_MAC_DEFAULT = "00:00:00:00:00:00" ;
	
	
	
	public static final String PROPERTIES_FILE = "intel.agent.demo.properties" ;
	public static final String PROP_C8Y_SERVER_URL = "c8y.server.url" ;
	public static final String PROP_TENNANT = "tennant.name" ;
	public static final String PROP_ADMIN_NAME = "admin.name" ;
	public static final String PROP_ADMIN_PASS = "admin.password" ;
	public static final String PROP_APPLICATION_KEY = "application.key" ;
	public static final String PROP_READING_PERIOD = "reading.period" ;
	
	public static final String ACCELEROMETER_X_VALUE = "accel.data.x" ; 
	public static final String ACCELEROMETER_Y_VALUE = "accel.data.y" ; 
	public static final String ACCELEROMETER_Z_VALUE = "accel.data.z" ;
	public static final String ACCELEROMETER_XYZ_VALUES = "accel.data.xyz" ;
	public static final String ACCELEROMETER_FULL_SCALE = "accel.full.scale" ;
	
	public static final String THRESHOLD_NEG_X = "threshold.negative.x" ;
	public static final String THRESHOLD_POS_X = "threshold.positive.x" ;
	public static final String THRESHOLD_NEG_Y = "threshold.negative.y" ;
	public static final String THRESHOLD_POS_Y = "threshold.positive.y" ;
	public static final String THRESHOLD_NEG_Z = "threshold.negative.z" ;
	public static final String THRESHOLD_POS_Z = "threshold.positive.z" ;
	
	public static final String ALARM_TIME_THRESHOLD = "alarm.time.threshold" ; 

}
