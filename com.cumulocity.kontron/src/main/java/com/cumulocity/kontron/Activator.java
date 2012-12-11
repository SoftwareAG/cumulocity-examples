/*
 * Copyright 2012 Cumulocity GmbH 
 */

package com.cumulocity.kontron;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, M2M_Demo_Constants {

	public static Properties prop = new Properties();
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		InputStream bundleProps = getClass().getResourceAsStream(PROPERTIES_FILE);
		prop.load(bundleProps);

		try {
			InputStream userProps = new FileInputStream(PROPERTIES_FILE);
			prop.load(userProps);
		}  catch (FileNotFoundException x) {
			System.out.println("User properties \"" + PROPERTIES_FILE + "\" not found, ignoring.");
		}

        if (prop.getProperty(PROP_ADMIN_NAME).isEmpty()) {
    		System.out.println("Error : "+PROP_ADMIN_NAME+" is not defined inside the file "+PROPERTIES_FILE) ;
            System.exit(1) ;
        }
        if (prop.getProperty(PROP_ADMIN_PASS).isEmpty()) {
    		System.out.println("Error : "+PROP_ADMIN_PASS+" is not defined inside the file "+PROPERTIES_FILE) ;
            System.exit(1) ;
        } 
        
        System.out.println("      Cumulocity URL: " + prop.getProperty(PROP_C8Y_SERVER_URL)) ;
        System.out.println("              Tenant: " + prop.getProperty(PROP_TENNANT)) ;
        System.out.println("                User: " + prop.getProperty(PROP_ADMIN_NAME)) ;
        System.out.println("          Accel rate: " + Integer.parseInt(prop.getProperty(PROP_READING_PERIOD))) ;
 
        M2MKontronAgentRepresentation agentRep = M2MKontronAgentRepresentation.getInstance(prop) ;
    	if (! agentRep.isOK())  {
    		System.out.println("Error : Cannot create or retrieve agent managed object in Cumulocity Server.") ;
            System.exit(1) ;
    	}

    	System.out.println("         Agent MO ID: " + agentRep.getID()) ;
    	agentRep.setAlarmTimeThreshold(Integer.parseInt(prop.getProperty(ALARM_TIME_THRESHOLD))) ;

        System.out.println() ;
    	
    	AccelerometerReader ar = new AccelerometerReader(Integer.parseInt(prop.getProperty(PROP_READING_PERIOD)),
    			new AccelerometerThresholds(
    					Double.parseDouble(prop.getProperty(THRESHOLD_NEG_X)),
    					Double.parseDouble(prop.getProperty(THRESHOLD_POS_X)),
    					Double.parseDouble(prop.getProperty(THRESHOLD_NEG_Y)),		
    					Double.parseDouble(prop.getProperty(THRESHOLD_POS_Y)),
    					Double.parseDouble(prop.getProperty(THRESHOLD_NEG_Z)),
    					Double.parseDouble(prop.getProperty(THRESHOLD_POS_Z))		
    					),
    			new AccelerometerThresholdActionImpl()) ;
    	
    	ar.start() ;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
