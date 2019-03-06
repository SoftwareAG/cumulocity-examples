package com.cumulocity.snmp.model.gateway.client;

import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class ClientDataChangedEvent implements GatewayProvider {
    private final Gateway gateway;
    private final Device device;
    private final Register register;
    private final DateTime time;
    private final Object value;
    private final boolean isPolledData;
}
