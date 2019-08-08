package com.cumulocity.snmp.unittests.factory.platform;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.snmp.factory.platform.MeasurementFactory;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.gateway.type.mapping.MeasurementMapping;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementFactoryTest {

    @InjectMocks
    private MeasurementFactory measurementFactory;

    @Mock
    private PlatformRepresentationEvent event;

    @Test
    public void shouldStoreMeasurementDataWithoutStaticFragment() {
        Device device = mock(Device.class);
        GId gId = mock(GId.class);
        Register register = mock(Register.class);
        MeasurementMapping mapping = mock(MeasurementMapping.class);

        when(event.getDevice()).thenReturn(device);
        when(device.getId()).thenReturn(gId);
        when(event.getDate()).thenReturn(DateTime.now());
        when(event.getRegister()).thenReturn(register);
        when(event.getMapping()).thenReturn(mapping);
        when(event.getValue()).thenReturn(new Object());

        when(mapping.getType()).thenReturn("c8y_test");
        when(mapping.getSeries()).thenReturn("c8y_test");

        Optional<MeasurementRepresentation> result = measurementFactory.apply(event);

        verify(mapping, times(1)).getStaticFragmentsMap();
        assertTrue(result.get().hasProperty("c8y_test"));
        assertTrue(result.get().getType().equals("c8y_test"));
        assertFalse(result.get().hasProperty("staticFragments"));
        assertFalse(result.get().hasProperty("test_staticFragment_1"));
        assertFalse(result.get().hasProperty("test_staticFragment_2"));
    }

    @Test
    public void shouldStoreMeasurementDataWithStaticFragment() {
        Device device = mock(Device.class);
        GId gId = mock(GId.class);
        Register register = mock(Register.class);
        MeasurementMapping mapping = mock(MeasurementMapping.class);
        Map<String, Map> staticFragmentsMap = new HashMap<>();
        staticFragmentsMap.put("test_staticFragment_1", Collections.EMPTY_MAP);
        staticFragmentsMap.put("test_staticFragment_2", Collections.EMPTY_MAP);

        when(event.getDevice()).thenReturn(device);
        when(device.getId()).thenReturn(gId);
        when(event.getDate()).thenReturn(DateTime.now());
        when(event.getRegister()).thenReturn(register);
        when(event.getMapping()).thenReturn(mapping);
        when(event.getValue()).thenReturn(new Object());

        when(mapping.getType()).thenReturn("c8y_test");
        when(mapping.getSeries()).thenReturn("c8y_test");
        when(mapping.getStaticFragmentsMap()).thenReturn(staticFragmentsMap);

        Optional<MeasurementRepresentation> result = measurementFactory.apply(event);

        verify(mapping, times(1)).getStaticFragmentsMap();
        assertTrue(result.get().hasProperty("c8y_test"));
        assertTrue(result.get().getType().equals("c8y_test"));
        assertTrue(result.get().hasProperty("test_staticFragment_1"));
        assertTrue(result.get().hasProperty("test_staticFragment_2"));
    }
}