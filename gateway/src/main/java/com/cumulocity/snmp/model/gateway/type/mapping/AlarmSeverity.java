package com.cumulocity.snmp.model.gateway.type.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum  AlarmSeverity {
    WARNING, MINOR, MAJOR, CRITICAL;

    public static String asString(AlarmSeverity severity) {
        if (severity == null) {
            return null;
        }
        return severity.name();
    }

    @JsonCreator
    public static AlarmSeverity fromString(String string) {
        for (final AlarmSeverity status : AlarmSeverity.values()) {
            if (status.name().equalsIgnoreCase(string)) {
                return status;
            }
        }
        return null;
    }
}
