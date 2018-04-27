package com.cumulocity.route.model.core;

import com.cumulocity.rest.representation.event.EventRepresentation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static com.cumulocity.route.configuration.ObjectMapperConfiguration.baseObjectMapper;

@Getter
@RequiredArgsConstructor
public class EventCreated extends HasEvent {
    private final EventRepresentation event;

    public boolean eventTypeEquals(String type) {
        return getEvent().getType().equals(type);
    }

    @SneakyThrows
    public String toString() {
        return getClass().getSimpleName() + "(event:" + baseObjectMapper.writeValueAsString(getEvent()) + ")";
    }
}
