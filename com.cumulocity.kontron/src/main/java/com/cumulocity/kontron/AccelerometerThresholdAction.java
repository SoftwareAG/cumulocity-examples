/*
 * Copyright 2012 Cumulocity GmbH 
 */

package com.cumulocity.kontron;

public interface AccelerometerThresholdAction
{
	public void action (AccelerometerXYZReading xyz, AccelerometerThresholds thr) ;
}
