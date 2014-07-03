package com.cumulocity.tixi.server.model;

import com.cumulocity.model.ID;

public class SerialNumber extends ID {

    private static final long serialVersionUID = 1L;

    public SerialNumber(String value) {
        super("c8y_SerialNumber", value);
    }
}
