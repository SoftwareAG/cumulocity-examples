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

import java.util.Date;

public class AccelerometerReader implements Runnable
{

	private int raw_accel_x = 0 ;
	private int raw_accel_y = 0 ;
	private int raw_accel_z = 0 ;
	private double accel_x = 0 ;
	private double accel_y = 0 ;
	private double accel_z = 0 ;
	
	private Date lastReading = null ;
	private CLIService cli = null ;
	private int readingPeriod = 333 ;
	private boolean running = true ;
	private int scale = 0 ;
	private int gscale_div = 4096 ;
	private AccelerometerThresholds thresholds = null ;
	private AccelerometerThresholdAction thrAction = null ;
	
	
	public AccelerometerReader (int readingPeriod, AccelerometerThresholds thr, AccelerometerThresholdAction action) 
	{
		this.readingPeriod = readingPeriod ;
		thresholds = thr ;
		thrAction = action ;
		cli = new CLIService() ;
		
		// read current scale (sensitivity) - 2g, 4g or 8g
		String line = cli.readFile(Activator.prop.getProperty(M2M_Demo_Constants.ACCELEROMETER_FULL_SCALE)) ;
		scale = Integer.parseInt(line) ;
		switch (scale)
		{
			default :
			case 8 : gscale_div = 4096 ; break ;
			case 4 : gscale_div = 8192 ; break ;
			case 2 : gscale_div = 16384 ; break ;
		}
		
		System.out.println("AccelerometerReader : current scale = " + scale + "g") ;
	}
	
	
	public void start () 
	{
		 Thread t = new Thread(this, "AccelerometerReader") ;
		 t.start() ;
	}
	
	
	public synchronized void stop () 
	{
		running = false ;
	}

	
	public void run ()
	{
		System.out.println(" -> AccelerometerReader started") ;

		while (running)
		{
			String line = cli.readFile(Activator.prop.getProperty(M2M_Demo_Constants.ACCELEROMETER_XYZ_VALUES)) ;
			//System.out.println(" AccelerometerReader : line = " + line) ;
			String[] values = line.split(" ") ;
			storeData(Integer.parseInt(values[1].trim()), Integer.parseInt(values[2].trim()), Integer.parseInt(values[3].trim())) ;
			
            try {
                Thread.sleep(readingPeriod);
            } catch (InterruptedException ie){
                System.err.println("AccelerometerReader : Child thread interrupted! " + ie.getMessage()) ;
            }
			
		}
		System.out.println(" <- AccelerometerReader stopped") ;
		
	}
	
	
	private synchronized void storeData (int x, int y, int z)
	{
		raw_accel_x = x ; 
		raw_accel_y = y ;
		raw_accel_z = z ;
		accel_x = (double)raw_accel_x / gscale_div ;
		accel_y = (double)raw_accel_y / gscale_div ;
		accel_z = (double)raw_accel_z / gscale_div ;
		lastReading = new Date() ;
		
		if (thresholds != null && thresholds.isSet) 
		{
			if (accel_x < -thresholds.negative_x || accel_x > thresholds.positive_x ||
				accel_y < -thresholds.negative_y || accel_y > thresholds.positive_y ||
				accel_z < -thresholds.negative_z || accel_z > thresholds.positive_z  )
			{
				// TO DO : to be launched in another thread
				thrAction.action(new AccelerometerXYZReading(lastReading, accel_x, accel_y, accel_z), thresholds) ;
			}
			
		}
		
		
	}
	
	
	public synchronized double getX () { return accel_x ; } 
	public synchronized double getY () { return accel_y ; } 
	public synchronized double getZ () { return accel_z ; } 
	
	
	public synchronized long getLastReading () 
	{
		return lastReading.getTime() ;
	}

	
	public synchronized AccelerometerXYZReading getReading ()
	{
		return new AccelerometerXYZReading(lastReading, accel_x, accel_y, accel_z) ;
	}
	
}
