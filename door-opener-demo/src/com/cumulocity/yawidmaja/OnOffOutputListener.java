package com.cumulocity.yawidmaja;


public class OnOffOutputListener implements InputChangedListener {

	private YawidMajaInitializer yawidMajaInitializer;
	private int forOutputPort;
	
	public OnOffOutputListener(YawidMajaInitializer yawidMajaInitializer, int forOutputPort) {
		this.yawidMajaInitializer = yawidMajaInitializer;
		this.forOutputPort = forOutputPort;
	}
	public void inputChangedTo(boolean isHigh) {
		if (isHigh) {
			yawidMajaInitializer.closeOutput(forOutputPort);
		}
		else {
			yawidMajaInitializer.openOutput(forOutputPort);
		}
	}

}
