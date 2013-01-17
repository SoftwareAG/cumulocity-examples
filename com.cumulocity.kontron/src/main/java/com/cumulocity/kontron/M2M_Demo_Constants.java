/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * Copyright 2012 Cumulocity GmbH 
 */

package com.cumulocity.kontron;

public interface M2M_Demo_Constants
{
	public static final String KONTRON_AGENT_TYPE = "com.cumulocity.KontronAgent" ;
	public static final String KONTRON_AGENT_ETH0_MAC_PROP = "eth0.mac.address" ;
	public static final String KONTRON_AGENT_DEFAULT_NAME = "Kontron Smart Agent" ;
	public static final String KONTRON_AGENT_EXTERNAL_ID = "KontronSmartAgent" ;
	public static final String KONTRON_AGENT_EXTERNAL_TYPE = "com.cumulocity.kontronagent.MacId" ;
	public static final String FRI_ETH0_MAC_FILE = "/sys/class/net/eth0/address" ;
	public static final String FRI_ETH0_MAC_DEFAULT = "00:00:00:00:00:00" ;
	
	public static final String PROPERTIES_FILE = "kontron.agent.properties" ;
	public static final String PROP_C8Y_SERVER_URL = "c8y.server.url" ;
	public static final String PROP_TENNANT = "tenant.name" ;
	public static final String PROP_ADMIN_NAME = "user.name" ;
	public static final String PROP_ADMIN_PASS = "user.password" ;
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
