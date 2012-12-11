/*
 * Copyright 2012 Nokia Siemens Networks 
 */

package com.cumulocity.kontron;

import java.text.DecimalFormat;
import java.util.Date;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public class AccelerometerThresholdActionImpl implements AccelerometerThresholdAction
{
	public void action (AccelerometerXYZReading xyz, AccelerometerThresholds thr)
	{
		//String message = "thresholds x:(-" +  thr.negative_x + ", " + thr.positive_x + ") " +
		//		"y:(-" +  thr.negative_y + ", " + thr.positive_y + ") " +
		//		"z:(-" +  thr.negative_z + ", " + thr.positive_z + ") " +
		//		"  XYZ reading : " + xyz.getX() + " " + xyz.getY() + " " + xyz.getZ() ;
		
		DecimalFormat df = new DecimalFormat("#.##") ;
		StringBuffer msgBuffer = new StringBuffer("Threshold crossed: ") ;
		if ( xyz.getX() < -thr.negative_x || xyz.getX() > thr.positive_x )
			msgBuffer.append("X=" + df.format(xyz.getX()) + " (-" + df.format(thr.negative_x) + ", " + df.format(thr.positive_x) +")  " ) ;
		if ( xyz.getY() < -thr.negative_y || xyz.getY() > thr.positive_y )
			msgBuffer.append("Y=" + df.format(xyz.getY()) + " (-" + df.format(thr.negative_y) + ", " + df.format(thr.positive_y) +")  " ) ;
		if ( xyz.getZ() < -thr.negative_z || xyz.getZ() > thr.positive_z )
			msgBuffer.append("Z=" + df.format(xyz.getZ()) + " (-" + df.format(thr.negative_z) + ", " + df.format(thr.positive_z) +")  " ) ;
		
		String message = msgBuffer.toString() ;
		System.out.println(message) ;
		
		AlarmRepresentation alarm = new AlarmRepresentation();
		alarm.setType("com.cumulocity.kontron.AccelerometerThreshold") ;
		alarm.setSeverity("Major");
		alarm.setText(message);
		alarm.setStatus("Active");
		alarm.setTime(new Date()); // set time to now
		
		M2MKontronAgentRepresentation.getInstance().sendAlarm(alarm) ;
		
	}

}
