package com.cumulocity.yawidmaja;

import com.cinterion.io.InPortListener;

public class YawidMajaInterruptInPortListener implements InPortListener {

	private YawidMajaInitializer yawidMajaInitializer;

	private boolean[] oldInputs = new boolean[8];
	private boolean[] newInputs = new boolean[8];
	
	//in this iteration, there can be at most one InputChangedListener per input
	private InputChangedListener[] inputChangedListeners = new InputChangedListener[8]; 
	
	public YawidMajaInterruptInPortListener(
			YawidMajaInitializer yawidMajaInitializer) {
		this.yawidMajaInitializer = yawidMajaInitializer;
		
		System.out.println("Constructing a yawidMajaInterruptInPortListener");
	}

	public void portValueChanged(int portValue) {
		//System.out.println("portValue changed to: " + portValue); //always seems to be zero
		
		boolean wasSuccessful = yawidMajaInitializer.sendI2CCommand("98");
		if (wasSuccessful) {
			String currentRawInputsStateI2CResponse = yawidMajaInitializer.getI2CData(2);
			//System.out.println("I2C inputs response:" + currentRawInputsStateI2CResponse);	
			System.out.println("InPort change detected. Delegating to new Thread for InputChangedListener calling");
			(  new Thread( new ProcessI2CResponseAndAndRunInputChangedListenersRunnable(currentRawInputsStateI2CResponse) )  ).start();
		}
		else {
			System.err.println("Couldn't successfully initiate preparation for reading input port data");
		}

	}
	
	public void registerInputChangedListener(int majaInputNumber, InputChangedListener newInputChangedListener) {
		int javaInputNumber = majaInputNumber - 1; //Yawid Maja inputs are indexed 1,2,3,4 . Here, these input numbers are converted to conventional zero-based indices.
		System.out.println("Registering an inputChangedListener");
		inputChangedListeners[javaInputNumber] = newInputChangedListener;
	}

	private class ProcessI2CResponseAndAndRunInputChangedListenersRunnable implements Runnable {

		String currentRawInputsStateI2CResponse;
		
		public ProcessI2CResponseAndAndRunInputChangedListenersRunnable(String currentRawInputsStateI2CResponse) {
			this.currentRawInputsStateI2CResponse = currentRawInputsStateI2CResponse;
		}
		public void run() {

			if (currentRawInputsStateI2CResponse.startsWith("{b+", 0) == true) {
				String hexadecimalInputsRepresentation = currentRawInputsStateI2CResponse
						.substring(3, 5);
				int inputsStatuses = Integer.valueOf(hexadecimalInputsRepresentation,
						16).intValue();

				for (int i = 0; i < 8; i++) {
					newInputs[i] = 
							((inputsStatuses & (1 << i)) == 0 ) ? 
									false : 
									true;
					//System.out.print(newInputs[i]);
				}
				//System.out.println();
				
				// if changes between oldInputs and newInputs then fire listeners for each input
				for (int i = 0; i < 8; i++) {
					// System.out.println("Checking stateChange for input " + i);
					if(
						newInputs[i] != oldInputs[i] && // if this input port has changed and...
						null != inputChangedListeners[i] // ... if there is an InputChangedListener registered for this input port
					) {
						// System.out.println("Change detected, therefore calling registered InputChangeListener...");
						inputChangedListeners[i].inputChangedTo( newInputs[i] ); // then notify that InputChangedListener
					}
					else {
						// System.out.println("No action required for input " + i);
					}
				}
				System.arraycopy(newInputs, 0, oldInputs, 0, 8); // new is the new old
			} else {
				System.err.println("Unexpected I2C response: " + currentRawInputsStateI2CResponse);
			}

		}
		
	}
}
