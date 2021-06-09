/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.telic.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FixType {

    _1D("1", "No Fix"), 
    _2D("2", "2D Fix"), 
    _3D("3", "3D Fix"),
    _6D("6", " GSM-Tracking");
    
    private static Logger logger = LoggerFactory.getLogger(FixType.class);

    private final String value;

    private final String label;

    private FixType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public static FixType forValue(String value) {
        for (FixType fixType : FixType.values()) {
            if (fixType.getValue().equals(value)) {
                return fixType;
            }
        }
        logger.debug("Unexpected FixType value = {}.", value);
        return null;
    }

}
