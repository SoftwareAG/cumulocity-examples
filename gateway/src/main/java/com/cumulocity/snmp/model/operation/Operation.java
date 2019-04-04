package com.cumulocity.snmp.model.operation;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.model.ExtensibleRepresentationView;
import com.cumulocity.snmp.model.gateway.Gateway;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Wither
@AllArgsConstructor
@NoArgsConstructor
@ExtensibleRepresentationView(fragment = Gateway.c8y_SetRegister, type = Gateway.TYPE)
public class Operation {
    public static final String SNMP_DEVICE = "c8y_SNMPDevice";

    private GId id;
    private Object value;

    public Double doubleValue() {
        if (value == null) {
            return null;
        }
        if (Number.class.isInstance(value)) {
            return ((Number) value).doubleValue();
        }
        return new Double(String.valueOf(value));
    }

    public Integer intValue() {
        if (value == null) {
            return null;
        }
        if (Number.class.isInstance(value)) {
            return ((Number) value).intValue();
        }
        return new Integer(String.valueOf(value));
    }

    public Long longValue() {
        if (value == null) {
            return null;
        }
        if (Number.class.isInstance(value)) {
            return ((Number) value).longValue();
        }
        return new Long(String.valueOf(value));
    }

    public String stringValue() {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

}
