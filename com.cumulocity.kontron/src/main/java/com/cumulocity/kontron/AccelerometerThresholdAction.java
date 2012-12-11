/*
 * Copyright 2012 Nokia Siemens Networks 
 */

package com.cumulocity.kontron;

public interface AccelerometerThresholdAction
{
	public void action (AccelerometerXYZReading xyz, AccelerometerThresholds thr) ;
}
