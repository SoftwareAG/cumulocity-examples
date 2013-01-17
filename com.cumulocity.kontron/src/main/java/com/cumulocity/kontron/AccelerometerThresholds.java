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
