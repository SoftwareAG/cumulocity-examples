package c8y.lx.driver;

import java.util.Date;

import c8y.SupportedMeasurements;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public abstract class MeasurementPollingDriver extends PollingDriver {

	private MeasurementApi measurements;
	private MeasurementRepresentation measurementRep = new MeasurementRepresentation();

	public MeasurementPollingDriver(String measurementType, String pollingProp,
			long defaultPollingInterval) {
		super(measurementType, pollingProp, defaultPollingInterval);
		measurementRep.setType(measurementType);
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		super.initialize(platform);
		this.measurements = platform.getMeasurementApi();
	}
	
	public void setSource(ManagedObjectRepresentation mo) {
		SupportedMeasurements sm = mo.get(SupportedMeasurements.class);

		if (sm == null) {
			sm = new SupportedMeasurements();
			mo.set(sm);
		}

		if (!sm.contains(measurementRep.getType())) {
			sm.add(measurementRep.getType());
		}

		measurementRep.setSource(mo);
	}
	
	protected void sendMeasurement(Object measurement) {
		try {
			measurementRep.set(measurement);
			measurementRep.setTime(new Date());
			measurements.create(measurementRep);
		} catch (SDKException e) {
			logger.warn("Cannot send measurement", e);
		}
	}
}
