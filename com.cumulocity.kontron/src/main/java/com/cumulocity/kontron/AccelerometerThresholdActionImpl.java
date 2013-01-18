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

package com.cumulocity.kontron;

import java.text.DecimalFormat;
import java.util.Date;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public class AccelerometerThresholdActionImpl implements AccelerometerThresholdAction
{
	public void action (AccelerometerXYZReading xyz, AccelerometerThresholds thr)
	{
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
