package com.cumulocity.me.example.cinterion;

import com.cumulocity.me.rest.convert.base.BaseRepresentationConverter;
import com.cumulocity.me.rest.json.JSONObject;

public class SignalStrengthSensorConverter extends BaseRepresentationConverter {

	private static final String PROP_TYPE = "type";
    private static final String PROP_UNIT = "unit";
    private static final String PROP_READING = "reading";
	
	protected Class supportedRepresentationType() {
		return SignalStrengthSensor.class;
	}

	public JSONObject toJson(Object representation) {
		JSONObject json = new JSONObject();
		SignalStrengthSensor sensor = (SignalStrengthSensor) representation;
		putString(json, PROP_TYPE, sensor.getType());
        putString(json, PROP_UNIT, sensor.getUnit());
        putString(json, PROP_READING, sensor.getReading());
		return json;
	}

	public Object fromJson(JSONObject json) {
		SignalStrengthSensor sensor = new SignalStrengthSensor();
		sensor.setType(getString(json, PROP_TYPE));
		sensor.setUnit(getString(json, PROP_UNIT));
		sensor.setReading(getString(json, PROP_READING));
		return sensor;
	}
}
