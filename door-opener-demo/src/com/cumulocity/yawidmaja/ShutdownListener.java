package com.cumulocity.yawidmaja;

public class ShutdownListener implements InputChangedListener {

	private DoorOpenerDemo toBeShutdownMIDlet;
	
	public ShutdownListener(DoorOpenerDemo toBeShutdownMIDlet) {
		this.toBeShutdownMIDlet = toBeShutdownMIDlet;
	}
	public void inputChangedTo(boolean isHigh) {
		if(isHigh) {
			System.out.println("Received command to shutdown MIDlet");
			toBeShutdownMIDlet.destroyApp(true);
		}
	}

}
