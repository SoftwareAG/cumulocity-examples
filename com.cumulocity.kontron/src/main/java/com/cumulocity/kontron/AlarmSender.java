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
