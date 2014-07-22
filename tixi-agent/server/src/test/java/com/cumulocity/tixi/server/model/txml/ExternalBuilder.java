package com.cumulocity.tixi.server.model.txml;

import com.cumulocity.tixi.server.model.txml.External.Bus;
import com.cumulocity.tixi.server.model.txml.External.Device;
import com.cumulocity.tixi.server.model.txml.External.Meter;

public class ExternalBuilder {
	
	private External external = new External();
	private Bus bus;
	private Device device;
	
	public static ExternalBuilder anExternal() {
		return new ExternalBuilder();
	}
	
	public ExternalBuilder withBus(String name) {
		bus = new Bus(name);
		external.getBuses().add(bus);
		return this;
	}
	
	public ExternalBuilder withDevice(String name) {
		device = new Device(name);
		bus.getDevices().add(device);
		return this;
	}
	
	public ExternalBuilder withMeter(String id) {
		device.getMeters().add(new Meter(id));
		return this;
	}
	
	public External build() {
		return external;
	}
}
