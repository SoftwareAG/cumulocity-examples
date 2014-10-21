package com.cumulocity.yawidmaja;

import java.util.Timer;
import java.util.TimerTask;

import com.cumulocity.me.smartrest.client.SmartConnection;
import com.cumulocity.me.smartrest.client.SmartResponse;
import com.cumulocity.me.smartrest.client.SmartResponseEvaluator;
import com.cumulocity.me.smartrest.client.impl.SmartRow;


public class DoorOpenerSmartResponseEvaluator implements SmartResponseEvaluator {

	private YawidMajaInitializer yawidMajaInitializer;
	private int forOutputPort;

	private String operationId = null;
	private String operationStatus = null;
	private String duration = null;

	public DoorOpenerSmartResponseEvaluator(YawidMajaInitializer yawidMajaInitializer, int forOutputPort, SmartConnection smartConnection) {
		this.yawidMajaInitializer = yawidMajaInitializer;
		this.forOutputPort = forOutputPort;
	}

	public void evaluate(SmartResponse smartResponse) {
		yawidMajaInitializer.setLed2();
		
		SmartRow[] smartRows = smartResponse.getDataRows();
		for (int i = 0; i < smartRows.length; i++) {
			SmartRow smartRow = smartRows[i];
			String[] dataArray = smartRow.getData();
			
			if( 500 == smartRow.getMessageId() ) {
				operationId = dataArray[0];
			}
			else if( 501 == smartRow.getMessageId() ) {
				operationStatus = dataArray[0];
				duration = dataArray[1];
			}
			else if( 502 == smartRow.getMessageId() ) {
			}
		}
		
		if (null != operationId && null != duration && operationStatus.equals("PENDING") ) {
			System.out.println("PENDING");
			
			System.out.println("Closing output port " + forOutputPort);
			yawidMajaInitializer.closeOutput(forOutputPort);
			TimerTask turnOffTimerTask = new TurnOffTimerTask();
			Timer timer = new Timer();
			timer.schedule(turnOffTimerTask, 600);
			
		}
		else if ( operationStatus.equals("EXECUTING") ) {
			System.out.println("EXECUTING");
		}
		else if ( operationStatus.equals("SUCCESSFUL") ) {
			System.out.println("SUCCESSFUL");
		}
		else {
			System.out.println("Unhandled operationStatus: " + operationStatus);
		}
	}
	
	
	private class TurnOffTimerTask extends TimerTask {
		
		public void run() {
			System.out.println("Opening output port " + forOutputPort);
			yawidMajaInitializer.openOutput(forOutputPort);
			yawidMajaInitializer.clearLed2();
		}
	}

}
