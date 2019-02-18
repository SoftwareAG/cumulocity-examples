package com.cumulocity.snmp.model.notification.platform;

import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class PlatformRepresentationEvent {
    private final DateTime date;
    private final Gateway gateway;
    private final Device device;
    private final Register register;
    private final Mapping mapping;
    private final Object value;
}
