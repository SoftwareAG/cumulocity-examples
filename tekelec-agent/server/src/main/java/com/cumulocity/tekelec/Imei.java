package com.cumulocity.tekelec;

import com.cumulocity.model.ID;

public class Imei extends ID {

    private static final long serialVersionUID = 1L;
    
    public Imei(String value) {
        super("c8y_Imei", value);
    }

}
