package com.cumulocity.snmp.model.core;

public interface ConfigEvent {

    String getMessage();

    ConfigEventType getType();
}
