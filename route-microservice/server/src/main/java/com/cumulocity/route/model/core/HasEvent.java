package com.cumulocity.route.model.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import lombok.SneakyThrows;

import java.util.Objects;

import static com.cumulocity.route.configuration.ObjectMapperConfiguration.baseObjectMapper;

public abstract class HasEvent extends HasSource {
    public abstract EventRepresentation getEvent();

    public GId getSource() {
        if (getEvent() != null && getEvent().getSource() != null) {
            return getEvent().getSource().getId();
        }
        return null;
    }

    @SneakyThrows
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName());
        if (getEvent() != null) {
            result.append("(event:").append(baseObjectMapper.writeValueAsString(getEvent())).append(")");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        HasEvent other = (HasEvent) obj;
        if (!Objects.equals(getSource(), other.getSource())) {
            return false;
        }
        if (!Objects.equals(getEvent().getAttrs(), other.getEvent().getAttrs())) {
            return false;
        }
        return true;
    }
}
