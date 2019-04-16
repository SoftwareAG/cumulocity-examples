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

}
