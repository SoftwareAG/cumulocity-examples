package com.cumulocity.yawidmaja;

import java.util.Timer;
import java.util.TimerTask;

public class OnForSomeTimeOutputListener implements InputChangedListener {

	private YawidMajaInitializer yawidMajaInitializer;
	private int forOutputPort;
	private long millisecondsUntilOff;
	
	public OnForSomeTimeOutputListener(YawidMajaInitializer yawidMajaInitializer, int forOutputPort, long millisecondsUntilOff) {
		this.yawidMajaInitializer = yawidMajaInitializer;
		this.forOutputPort = forOutputPort;
		this.millisecondsUntilOff = millisecondsUntilOff;
	}
	
	public void inputChangedTo(boolean isHigh) {
		if(isHigh) {
			System.out.println("Closing output port " + forOutputPort);
			yawidMajaInitializer.closeOutput(forOutputPort);
			
			TimerTask turnOffTimerTask = new TurnOffTimerTask();
			Timer timer = new Timer();
			timer.schedule(turnOffTimerTask, millisecondsUntilOff);
		}

	}
	
	private class TurnOffTimerTask extends TimerTask {

		public void run() {
			System.out.println("Opening output port " + forOutputPort);
			yawidMajaInitializer.openOutput(forOutputPort);
		}
		
	}

}
