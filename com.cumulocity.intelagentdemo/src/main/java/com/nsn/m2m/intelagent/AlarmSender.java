/*
 * Copyright 2012 Nokia Siemens Networks 
 */

package com.nsn.m2m.intelagent;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;



public class AlarmSender implements Runnable
{
	private AlarmRepresentation alarm ;
	private AlarmApi alarmApi ;
	
	
	public AlarmSender (AlarmRepresentation alarm, AlarmApi alarmApi)
	{
		this.alarm = alarm ;
		this.alarmApi = alarmApi ;
	}
	
	
	public void sendAlarm ()
	{
		 Thread t = new Thread(this, "AlarmSender") ;
		 t.start() ;
	}
	
	
	@Override
	public void run()
	{
        try {
            System.out.println("Info: Sending alarm");
            alarmApi.create(alarm);
            System.out.println("Info: Alarm created ");

        } catch (SDKException e) {
            System.out.println("There is a problem connecting to the platform: " + e.getMessage());
        }   		
	}

}
