package com.cumulocity.yawidmaja;


import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.cinterion.io.BearerControl;
import com.cumulocity.me.smartrest.client.SmartConnection;
import com.cumulocity.me.smartrest.client.SmartRequest;
import com.cumulocity.me.smartrest.client.SmartResponse;
import com.cumulocity.me.smartrest.client.SmartResponseEvaluator;
import com.cumulocity.me.smartrest.client.impl.SmartCometClient;
import com.cumulocity.me.smartrest.client.impl.SmartRequestImpl;
import com.cumulocity.me.smartrest.client.impl.SmartRow;

import com.cumulocity.me.smartrest.client.impl.YawidMaja2SmartHttpConnection;
import com.cumulocity.me.util.StringUtils;

public class DoorOpenerDemo extends MIDlet {

	private final static String DOOR_OPENER_SMART_REST_TEMPLATES = 
			// a SmartREST request for getting a managed object with a specified ID
			"10,100,GET,/inventory/managedObjects/&&,,,&&,,\n" +
			
			// a SmartREST request for creating a new apartment with a specified name and a specified duration in milliseconds expressing how long the virtual door opener button is kept pressed.
			"10,200,POST,/inventory/managedObjects,application/vnd.com.nsn.cumulocity.managedObject+json,application/vnd.com.nsn.cumulocity.managedObject+json,&&,,\"{\"\"name\"\":\"\"&&\"\",\"\"c8y_IsDevice\"\":{},\"\"c8y_PushButton\"\":{\"\"duration\"\":&&},\"\"com_cumulocity_model_Agent\"\":{}}\"\n" +
			
			// a SmartREST request for creating a new door opener button with a specified name.
			"10,201,POST,/inventory/managedObjects,application/vnd.com.nsn.cumulocity.managedObject+json,application/vnd.com.nsn.cumulocity.managedObject+json,&&,,\"{\"\"name\"\":\"\"&&\"\"}\"\n" +
			
			// a SmartREST request for creating an association between the apartment and a button. The first specified ID is the apartment ID. The second specified ID is the button ID.
			"10,202,POST,/inventory/managedObjects/&&/childDevices,application/vnd.com.nsn.cumulocity.managedObjectReference+json,,&&,,\"{\"\"managedObject\"\":{\"\"id\"\":\"\"&&\"\"}}\"\n" +
			
			// a SmartREST request for updating the current state of the door represented by the specified ID (i.e., "door is opening").
			"10,300,PUT,/devicecontrol/operations/&&,application/vnd.com.nsn.cumulocity.operation+json,,&&,,\"{\"\"status\"\":\"\"EXECUTING\"\"}\"\n" +
			
			//a SmartREST request for updating the current state of the door represented by the specified ID (i.e., "door was opened").
			"10,301,PUT,/devicecontrol/operations/&&,application/vnd.com.nsn.cumulocity.operation+json,,&&,,\"{\"\"status\"\":\"\"SUCCESSFUL\"\"}\"\n" +
	
			//a SmartREST response that matches on every response that includes an ID. Keep in mind that a single SmartREST request gets as a single HTTP response a bundle of SmartREST responses: The bundle consists of all matching SmartREST responses.
			"11,500,,,\"$.id\"\n" +
			
			//a SmartREST response that matches on every response that includes a status and a push button duration. This response is used for longpolling commands send from the Cumulocity host to this Cinterion device. Keep in mind that a single SmartREST request gets as a single HTTP response a bundle of SmartREST responses: The bundle consists of all matching SmartREST responses.
			"11,501,,,\"$.status\",\"$.c8y_PushButton.duration\"\n" +
			
			//a SmartREST ersponse that matches on every response that includes a button property.
			"11,502,,,\"$.c8y_PushButton.button\"";
	
	
	//the unique ID under which the DOOR_OPENER_SMART_REST_TEMPLATES are keyed on the Cumulocity platform.
	private final static String X_ID = "doorOpenerRestTemplates";
	
	//file which contains APN configuration, e.g.: ;bearer_type=gprs;access_point=m2m.tele2.com;username=anyone;password=something
	private final static String APN_CONFIG_TXT = "apn_config.txt";
	
	//file which contains information about which Cumulocity host to connect to and containing bootstrap credentials. This file has to be copied onto this device before running this demo.
	private final static String CUMULOCITY_CONFIG_TXT = "cumulocity_config.txt";
	
	//file which contains individual device user credentials, the associated apartment managedObject ID, and 
	private final static String DEVICE_USER_CONFIG_TXT = "device_user_config.txt";
	
	private YawidMajaInitializer initializer;
	
	private SmartConnection smartConnection;
	private SmartConnection longPollingSmartConnection;
	private SmartCometClient smartCometClient;
	private SmartResponseEvaluator doorOpenerSmartResponseEvaluator;
	
	public DoorOpenerDemo() {
		System.out.println("DoorOpenerDemo: constructor");
	}

	public void startApp() throws MIDletStateChangeException {

		System.out.println("DoorOpenerDemo: startApp()");
		
		initializer = new YawidMajaInitializer();
		initializer.initYawidMaja();
				
		makeLedsBlink(); //for visual output to see that headless start up works well

		System.out.println("Registering extended bearer listener...");
		BearerControl.addListenerEx( new StdoutReportingBearerControlListenerEx() );
		
		try {
			if ( ! initializer.fileExists(DEVICE_USER_CONFIG_TXT) ) {
				//bootstrapping
				System.out.println("No device user credentials found. Therefore bootstrapping once");
				
				if (  initializer.fileExists( CUMULOCITY_CONFIG_TXT )  ) {
					
					//COLLECT INITIAL CONFIGURATION DATA///////////////////////////////////////////
					String host = getHostFromFile();
					System.out.println("Using host: " + host);
					
					String bootstrapAuthorizationHeaderValue = getBootstrapAuthorizationHeaderValueFromFile();
					System.out.println("Using bootstrap authorization header value: " + bootstrapAuthorizationHeaderValue);
					
					SmartConnection bootstrapConnection = new YawidMaja2SmartHttpConnection(host, X_ID, bootstrapAuthorizationHeaderValue);
					System.out.println("Using connector parameters: " + getApnConnectorParametersFromFile() );
					bootstrapConnection.setupConnection( getApnConnectorParametersFromFile() );
					
					//BOOTSTRAP-REGISTER THIS DEVICE/////////////////////////////////////////////////////
					String imei = initializer.getImei();
					System.out.println("About to bootstrap-register this device at Cumulocity platform with IMEI: " + imei);
					String deviceUserAuthorizationHeaderValue = bootstrapConnection.bootstrap(imei);
					System.out.println("Successfully registered this device. Device user credentials in Authorization header encoding are: " + deviceUserAuthorizationHeaderValue);
					
					String deviceUserConfigContentToBeWritten = deviceUserAuthorizationHeaderValue + "\r\n";
					
					System.out.println("Creating SmartConnection with device user credentials...");
					smartConnection = new YawidMaja2SmartHttpConnection(host, X_ID, deviceUserAuthorizationHeaderValue);
					smartConnection.setupConnection( getApnConnectorParametersFromFile() );
					
					//REGISTER SMARTREST TEMPLATES/////////////////////////////////////////////
					System.out.println("Registering door opener demo's SmartREST templates collection...");
					String smartRestTemplatesManagedObjectId = smartConnection.templateRegistration(DOOR_OPENER_SMART_REST_TEMPLATES);
					System.out.println("Successfully registered SmartREST templates collection as managedObjectId " + smartRestTemplatesManagedObjectId);
					
					//CREATE APARTMENT////////////////////////////////////////////////////////
					System.out.println("Creating a new apartment");
					SmartRequest createApartmentRequest = new SmartRequestImpl(200, "Apartment" + imei + "," + 600); // create an apartment with name "ApartmentImeiNumber" and a button push duration of 600 ms
					SmartResponse createApartmentResponse = smartConnection.executeRequest(createApartmentRequest);
					System.out.println("Got a apartment creation response:");
					
					String apartmentId = null;
					System.out.println("-----------------------------");
					SmartRow[] apartmentSmartRows = createApartmentResponse.getDataRows();
					for (int i = 0; i < apartmentSmartRows.length; i++) {
						int messageId = apartmentSmartRows[i].getMessageId();
						System.out.print("messageId: " + messageId );
						String[] dataArray = apartmentSmartRows[i].getData();
						for (int j = 0; j < dataArray.length; j++) {
							System.out.print("  > " + dataArray[j] );
						}
						System.out.println();
						
						if (500 == messageId) {
							apartmentId = dataArray[0];
							System.out.println("Appending apartmentId " + apartmentId + " to " + DEVICE_USER_CONFIG_TXT + " content.");
							deviceUserConfigContentToBeWritten = deviceUserConfigContentToBeWritten + apartmentId + "\r\n";
						}
						
					}
					System.out.println("-----------------------------\n");
					
					//CREATE BUTTON////////////////////////////////////////////////////////
					System.out.println("Creating a new button");
					SmartRequest createButtonRequest = new SmartRequestImpl(201, "Door1");
					SmartResponse createButtonResponse = smartConnection.executeRequest(createButtonRequest);
					System.out.println("Got a button creation response:");
					
					String buttonId = null;
					System.out.println("-----------------------------");
					SmartRow[] buttonSmartRows = createButtonResponse.getDataRows();
					for (int i = 0; i < buttonSmartRows.length; i++) {
						int messageId = buttonSmartRows[i].getMessageId();
						System.out.print("messageId: " + messageId);
						String dataArray[] = buttonSmartRows[i].getData();
						for (int j = 0; j < dataArray.length; j++) {
							System.out.print("  > " + dataArray[j] );
						}
						System.out.println();
						
						if (500 == messageId) {
							buttonId = dataArray[0];
							System.out.println("Appending buttonId " + buttonId + " to " + DEVICE_USER_CONFIG_TXT + " content.");
							deviceUserConfigContentToBeWritten = deviceUserConfigContentToBeWritten + buttonId + "\r\n";
						}
						
					}
					System.out.println("-----------------------------\n");
					
					//MAKE BUTTON A CHILD OF APARTMENT////////////////////////////////////////////////////////
					System.out.println("Making button " + buttonId + " a child of apartment " + apartmentId + " ...");
					SmartRequest makeChildDeviceRequest = new SmartRequestImpl(202, apartmentId + "," + buttonId);
					smartConnection.executeRequest(makeChildDeviceRequest);
					System.out.println("Finished association...");
					
					//PERSIST DATA TO FILE SYSTEM////////////////////////////////////////////////////////
					System.out.println("Writing device user credentials, apartmentId, and buttonId to file system...");
					initializer.writeOrOverwritePlainTextFile(DEVICE_USER_CONFIG_TXT, deviceUserConfigContentToBeWritten);
					
					System.out.println("Finished bootstrapping");
				} // end bootstrapping
				else { // even CUMULOCITY_CONFIG_TXT does not exist
					throw new Exception("File " + CUMULOCITY_CONFIG_TXT + " does not exist");
				}
			}
			
			//BEGIN day-to-day start-up procedure
			System.out.println(DEVICE_USER_CONFIG_TXT + " exists. Using it to set up connections");
			
			String host = getHostFromFile();
			System.out.println("Host is: " + host);
			String deviceUserAuthorizationHeaderValue = getDeviceUserAuthorizationHeaderValueFromFile();
			System.out.println("Device user authorization header value is: " + deviceUserAuthorizationHeaderValue);
			
			smartConnection = new YawidMaja2SmartHttpConnection(host, X_ID, deviceUserAuthorizationHeaderValue);
			smartConnection.setupConnection( getApnConnectorParametersFromFile() );
			
			longPollingSmartConnection = new YawidMaja2SmartHttpConnection(host, X_ID, deviceUserAuthorizationHeaderValue);
			longPollingSmartConnection.setupConnection( getApnConnectorParametersFromFile() );
			
			System.out.println("Creating smartCometClient...");
			doorOpenerSmartResponseEvaluator = new DoorOpenerSmartResponseEvaluator(initializer, 2, smartConnection);

			smartCometClient = new SmartCometClient(longPollingSmartConnection, doorOpenerSmartResponseEvaluator);

			String apartmentManagedObjectId = getApartmentIdFromFile();
			
			System.out.println("Starting to listen for apartmentId " + apartmentManagedObjectId + " events via longpolling");
			smartCometClient.startListenTo("/cep/realtime", new String[]{"/operations/" + apartmentManagedObjectId} );
			
			initializer.setLed1();
			System.out.println("Finished startApp() ....");
		}
		catch (Exception e) {
			System.err.println("An error occured during initialization");
			e.printStackTrace();
		}
	}

	public void pauseApp() {
		System.out.println("DoorOpenerDemo: pauseApp()");
	}

	public void destroyApp(boolean cond) {
		System.out
				.println("DoorOpenerDemo: destroyApp(" + cond + ")");
		initializer.closeYawidMaja();
		notifyDestroyed();
	}
	
	private String getHostFromFile() throws Exception {
		String cumulocityConfigContent = initializer.readPlainTextFile(CUMULOCITY_CONFIG_TXT);
		String[] splittedCumulocityConfigContent = StringUtils.split(cumulocityConfigContent, "\r\n");
		String host = splittedCumulocityConfigContent[0];
		return host;
	}
	
	private String getBootstrapAuthorizationHeaderValueFromFile() throws Exception {
		String cumulocityConfigContent = initializer.readPlainTextFile(CUMULOCITY_CONFIG_TXT);
		
		String[] splittedCumulocityConfigContent = StringUtils.split(cumulocityConfigContent, "\r\n");
		String bootstrapAuthorization = splittedCumulocityConfigContent[1];
		return bootstrapAuthorization;
	}
	
	private String getDeviceUserAuthorizationHeaderValueFromFile() throws Exception {
		String deviceUserConfigContent = initializer.readPlainTextFile(DEVICE_USER_CONFIG_TXT);
		
		String[] splittedDeviceUserConfigContent = StringUtils.split(deviceUserConfigContent, "\r\n");
		String deviceUserAuthorization = splittedDeviceUserConfigContent[0];
		return deviceUserAuthorization;
	}
	
	private String getApartmentIdFromFile() throws Exception {
		String deviceUserConfigContent = initializer.readPlainTextFile(DEVICE_USER_CONFIG_TXT);
		
		String[] splittedDeviceUserConfigContent = StringUtils.split(deviceUserConfigContent, "\r\n");
		
		String apartmentId = splittedDeviceUserConfigContent[1];
		return apartmentId;
	}
	
	private String getApnConnectorParametersFromFile() throws Exception {
		String apnConfigContent = initializer.readPlainTextFile(APN_CONFIG_TXT);
		
		String[] splittedApnConfigContent = StringUtils.split(apnConfigContent, "\r\n");
		
		String apnConnectorParameters = splittedApnConfigContent[0];
		return apnConnectorParameters;
	}
	
	private void makeLedsBlink() {
		new Thread(new Runnable() {

			public void run() {
				try {
			 		initializer.setLed1();
					Thread.sleep(200);
					initializer.setLed2();
					Thread.sleep(200);
					initializer.setLed3();
					Thread.sleep(200);
					initializer.clearLed1();
					Thread.sleep(200);
					initializer.clearLed2();
					Thread.sleep(200);
					initializer.clearLed3();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	
}
