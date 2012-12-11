/*
 * Copyright 2012 Cumulocity GmbH 
 */

package com.cumulocity.kontron;

public class AccelerometerThresholds
{
	public double positive_x = 0 ;
	public double positive_y = 0 ;
	public double positive_z = 0 ;
	public double negative_x = 0 ;
	public double negative_y = 0 ;
	public double negative_z = 0 ;
	public boolean isSet = false ;
	
	
	public AccelerometerThresholds () { isSet = false ; }   // everyone is 0 -> no thresholds
	
	
	public AccelerometerThresholds (double thr_xyz)
	{ 
		positive_x = negative_x = 
		positive_y = negative_y = 
		positive_z = negative_z = thr_xyz ;
		isSet = true ;
	} 
	
	
	public AccelerometerThresholds (double thr_x, double thr_y, double thr_z)
	{ 
		positive_x = negative_x = thr_x ;
		positive_y = negative_y = thr_y ;
		positive_z = negative_z = thr_z ;
		isSet = true ;
	} 
	
	public AccelerometerThresholds (double nx, double px, double ny, double py, double nz, double pz)
	{ 
		positive_x = px ;
	    negative_x = nx ;
		positive_y = py ;
		negative_y = ny ;
		positive_z = pz ;
		negative_z = nz ;
		isSet = true ;
	} 
}
