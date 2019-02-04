package com.cumulocity.snmp.model.gateway.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    read, write;

    @JsonCreator
    public static Status fromString(String string) {
        for (final Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(string)) {
                return status;
            }
        }
        return null;
    }
}
