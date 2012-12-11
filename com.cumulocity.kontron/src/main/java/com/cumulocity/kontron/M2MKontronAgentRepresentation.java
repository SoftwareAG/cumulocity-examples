/*
 * Copyright 2012 Cumulocity GmbH 
 */

package com.cumulocity.kontron;

import java.util.Date;
import java.util.Properties;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PagedCollectionResource;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;


public class M2MKontronAgentRepresentation implements M2M_Demo_Constants
{

	private ManagedObjectRepresentation agentMO = null ;
	private Platform platform = null ;
	private InventoryApi inventoryApi = null ;
	private IdentityApi identityApi = null ;
	private AlarmApi alarmApi = null ;
	private String agentID = null ;
	private int alarm_time_threshold = 20000 ;   //default
	private Date lastAlarmDate = null ; 
	private static M2MKontronAgentRepresentation thisInstance = null ;
	private String eth0_mac = null ;
	
	
	public static M2MKontronAgentRepresentation getInstance (Properties props)
	{
		if (thisInstance == null) 
			thisInstance = new M2MKontronAgentRepresentation (props) ;
		return thisInstance ;
	}
	
	
	public static M2MKontronAgentRepresentation getInstance ()
	{
		return thisInstance ;
	}
	
	
	// find M2M device in C8Y, if not found creates it 
	private M2MKontronAgentRepresentation (Properties props)
	{
		if (props.getProperty(PROP_C8Y_SERVER_URL).equalsIgnoreCase("none"))
			return ;
		
        platform = new PlatformImpl(props.getProperty(PROP_C8Y_SERVER_URL),
    			props.getProperty(PROP_TENNANT), props.getProperty(PROP_ADMIN_NAME),
    			props.getProperty(PROP_ADMIN_PASS), props.getProperty(PROP_APPLICATION_KEY)) ;
        inventoryApi = platform.getInventoryApi() ; ;
        identityApi  = platform.getIdentityApi() ;
        alarmApi = platform.getAlarmApi() ;		
		
        // reading eth0 MAC
        CLIService cli = new CLIService() ;
        eth0_mac = cli.readFile(FRI_ETH0_MAC_FILE) ;
        if (eth0_mac == null) eth0_mac = FRI_ETH0_MAC_DEFAULT ;
        System.out.println("eth0 MAC address : " + eth0_mac) ;
        
		agentMO = getAgentRepresentation() ;
        if (agentMO == null)
        {
        	System.out.println("Kontron Agent MO not found. Attempting to create ...") ;
        	agentMO = createKontronAgent() ;
        	if (agentMO == null)
        	{
        		System.out.println("Fatal Error : Cannot create Agent") ;
         	}
        	createAgentExternalId(agentMO) ;
        }
        agentID = agentMO.getId().getValue() ;
	}

	
	public void setAlarmTimeThreshold (int threshold)
	{
		alarm_time_threshold = threshold ;
	}
	
	
	public String getID ()
	{
		return agentID ;
	}
	
	
	public boolean isOK ()
	{
		return agentMO != null ;
	}
	
	private ManagedObjectRepresentation createKontronAgent ()
    {
    	ManagedObjectRepresentation agentRet = null ;
    	ManagedObjectRepresentation agent = new ManagedObjectRepresentation() ;
    	agent.setName(KONTRON_AGENT_DEFAULT_NAME) ;
    	agent.setType(KONTRON_AGENT_TYPE); // including this one is not mandatory, but it provides meaningful information to user
    	agent.set(new com.cumulocity.model.Agent()); // agents must include this fragment
    	agent.setProperty(KONTRON_AGENT_ETH0_MAC_PROP, eth0_mac) ;
    	
    	try {
    		inventoryApi.create(agent) ;
    		agentRet = getAgentRepresentation () ;
            System.out.println("Kontron agent created, id = " + agentRet.getId().getValue()) ;
		}
    	catch (SDKException e) {
    		System.out.println(e.getMessage()) ;
			return null ;
		}
    	return agentRet ;
    	
    }
	
	
    private ExternalIDRepresentation createAgentExternalId (ManagedObjectRepresentation agent)
    {
    	ExternalIDRepresentation externalIDGid = new ExternalIDRepresentation();
    	externalIDGid.setType(KONTRON_AGENT_EXTERNAL_TYPE);
    	externalIDGid.setExternalId(KONTRON_AGENT_EXTERNAL_ID + "_" + eth0_mac);
    	externalIDGid.setManagedObject(agent);
    	ExternalIDRepresentation id = null ;
    	try {	    	
	    	id = identityApi.create(externalIDGid);
		}
    	catch (SDKException e) {
    		System.out.println(e.getMessage()) ;
			return null ;
		}
    	return id ;
    }	
	
	
    private ManagedObjectRepresentation getAgentRepresentation ()
    {
    	ManagedObjectRepresentation agent = null ;
        InventoryFilter inventoryFilter = new InventoryFilter();
        inventoryFilter.byType(KONTRON_AGENT_TYPE) ;
                
        try
        {
        	PagedCollectionResource<ManagedObjectCollectionRepresentation> pagedCollectionResource = 
        			inventoryApi.getManagedObjectsByFilter(inventoryFilter);
        	
        	ManagedObjectCollectionRepresentation mos;
        	for (mos = pagedCollectionResource.get(); mos != null && mos.getManagedObjects().size() > 0; mos = pagedCollectionResource.getNextPage(mos))
        	{
        		for (ManagedObjectRepresentation mo : mos.getManagedObjects())
        		{
        			if (mo.getName().equals(KONTRON_AGENT_DEFAULT_NAME) &&
        					mo.getProperty(KONTRON_AGENT_ETH0_MAC_PROP).equals(eth0_mac))
        				agent = mo ;
        			//System.out.println("agent = " + mo.getId().getValue()) ;
        		}
        	}
        }
        catch (SDKException e)
        {
        	System.out.println(e.getMessage()) ;
        	return null ;
        }
    	return agent ;
    }
	
    
    public boolean sendAlarm (AlarmRepresentation alarm)
    {
    	if (agentMO == null) return false;
    	
    	// check maximal alarm rate tolerance
    	Date now = new Date() ;
     	if (lastAlarmDate != null && now.getTime() - lastAlarmDate.getTime() < alarm_time_threshold )
    		return false ;
     	alarm.setSource(agentMO) ;
     	AlarmSender as = new AlarmSender(alarm, alarmApi) ;
     	as.sendAlarm() ;
        lastAlarmDate = now ;
    	return true ;
    }
    
}
