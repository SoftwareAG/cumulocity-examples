/*
 * Copyright 2012 Nokia Siemens Networks 
 */

package com.cumulocity.kontron;

import java.util.Date;

public class AccelerometerXYZReading
{
	private double x = 0 ;
	private double y = 0 ;
	private double z = 0 ;
	private Date date = null ;
	
	public AccelerometerXYZReading (Date date, double x, double y, double z)
	{
		this.x = x ;
		this.y = y ;
		this.z = z ;
		this.date = date ;
	}

	public double getX () { return x ; } 
	public double getY () { return y ; } 
	public double getZ () { return z ; }
	public Date getDate () { return date ; }

	public String toString ()
	{
		return date.toString() + " " + x + " " + y + " " + z ;
	}

	
}
