package com.cumulocity.snmp.utils;

import lombok.Getter;

public enum SnmpVariableType {
    INTEGER(2), COUNTER32(65), GAUGE(66), COUNTER64(70);

    @Getter
    private int type;

    SnmpVariableType(int type) {
        this.type = type;
    }
}
