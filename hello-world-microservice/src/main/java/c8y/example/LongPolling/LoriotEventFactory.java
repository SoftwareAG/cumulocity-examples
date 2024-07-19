package c8y.example.LongPolling;

import c8y.LoriotUplinkRequest;
import c8y.Position;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class LoriotEventFactory {

    private static String POSITION_EVENT_TYPE = "c8y_Position";
    private static String POSITION_EVENT_TEXT = "Location updated";
    private final static String EVENT_TYPE = "c8y_LoriotUplinkRequest";
    private final static String EVENT_TEXT = "New uplink message received";


    public EventRepresentation createEventForPosition(Position position,DateTime dateTime, ManagedObjectRepresentation deviceMo) {
        EventRepresentation event = new EventRepresentation();

        event.setType(POSITION_EVENT_TYPE);
        event.setText(POSITION_EVENT_TEXT);
        event.setDateTime(dateTime != null ? dateTime: new DateTime());
        event.setSource(deviceMo);
        event.set(position);
        return event;
    }

    public EventRepresentation createEvent(GId id, LoriotUplinkRequest data){
        EventRepresentation event = new EventRepresentation();
        event.setType(EVENT_TYPE);
        event.setDateTime(new DateTime());
        event.setSource(ManagedObjects.asManagedObject(id));
        event.setText(EVENT_TEXT);
        event.set(data);
        return event;
    }
}
