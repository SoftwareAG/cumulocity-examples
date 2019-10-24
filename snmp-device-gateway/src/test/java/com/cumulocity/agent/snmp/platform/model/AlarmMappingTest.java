package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlarmMappingTest {

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }


    @Test
    public void shouldBuildAlarmRepresentation() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        AlarmMapping mapping = new AlarmMapping();
        mapping.setType("c8y_Temperature");
        mapping.setText("SOME ALARM TEXT");
        mapping.setSeverity(AlarmSeverity.WARNING.name());

        AlarmRepresentation alarm = mapping.buildAlarmRepresentation(source);

        assertEquals(source.getId(), alarm.getSource().getId());
        assertEquals(DateTime.now(), alarm.getDateTime());
        assertEquals(mapping.getType(), alarm.getType());
        assertEquals(mapping.getText(), alarm.getText());
        assertEquals(mapping.getSeverity(), alarm.getSeverity());
    }
}