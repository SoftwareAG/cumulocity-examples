package com.nsn.m2m.intelagent;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;





public class Main implements M2M_Demo_Constants
{
	public static Properties prop = new Properties() ;

    public static void main( String[] args )
    {

        // <1> read properties file
    	try {
    		prop.load(new FileInputStream(M2M_Demo_Constants.PROPERTIES_FILE));
    	} catch (IOException ex) {
        	System.out.println("properties file not found : " + M2M_Demo_Constants.PROPERTIES_FILE) ;
            System.exit(1) ;
        } 

        System.out.println("-------------------------------------------") ;
        System.out.println("          INTEL SMART AGENT STARTED") ;
        System.out.println("-------------------------------------------") ;

        System.out.println("C8Y URL    : " + prop.getProperty(PROP_C8Y_SERVER_URL)) ;
        System.out.println("Tenant     : " + prop.getProperty(PROP_TENNANT)) ;
        System.out.println("user       : " + prop.getProperty(PROP_ADMIN_NAME)) ;
        System.out.println("accel rate : " + Integer.parseInt(prop.getProperty(PROP_READING_PERIOD))) ;
        System.out.println() ;
 
        M2MIntelAgentRepresentation agentRep = M2MIntelAgentRepresentation.getInstance(prop) ;
    	if (agentRep.isOK())  {
    		System.out.println("existing Intel Agent MO ID = " + agentRep.getID()) ;
    		agentRep.setAlarmTimeThreshold(Integer.parseInt(prop.getProperty(ALARM_TIME_THRESHOLD))) ;
    	}
    	else {
    		System.out.println("Error : Cannot create Agent") ;
    	}
        
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

   


}
