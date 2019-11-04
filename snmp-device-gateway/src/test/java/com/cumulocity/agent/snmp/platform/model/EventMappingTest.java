package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventMappingTest {

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }


    @Test
    public void shouldBuildEventRepresentation() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        EventMapping mapping = new EventMapping();
        mapping.setType("c8y_Temperature");
        mapping.setText("SOME EVENT TEXT");

        EventRepresentation event = mapping.buildEventRepresentation(source);

        assertEquals(source.getId(), event.getSource().getId());
        assertEquals(DateTime.now(), event.getDateTime());
        assertEquals(mapping.getType(), event.getType());
        assertEquals(mapping.getText(), event.getText());
    }
}