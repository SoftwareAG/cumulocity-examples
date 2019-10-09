package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementPublisherTest {

    @Mock
    private MeasurementPubSub measurementPubSub;

    @InjectMocks
    private MeasurementPublisher measurementPublisher;

    @Test
    public void shouldPublishAlarm() {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        measurement.setSource(source);
        measurement.setType("c8y_TemperatureMeasurement");
        measurement.setDateTime(DateTime.now());

        measurementPublisher.publish(measurement);

        Mockito.verify(measurementPubSub).publish(measurement.toJSON());
    }
}