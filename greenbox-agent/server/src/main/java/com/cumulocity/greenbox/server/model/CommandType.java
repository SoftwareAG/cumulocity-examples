package com.cumulocity.greenbox.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommandType {
    SETUP, SEND;

    @JsonCreator
    public static CommandType forValue(String value) {
        return valueOf(value.toUpperCase());
    }
}
