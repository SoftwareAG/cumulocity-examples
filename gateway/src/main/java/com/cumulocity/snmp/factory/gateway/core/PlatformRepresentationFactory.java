package com.cumulocity.snmp.factory.gateway.core;

import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.google.common.base.Optional;
import org.joda.time.DateTime;

public interface PlatformRepresentationFactory<M extends Mapping, R> {
    Optional<R> apply(DateTime date, Gateway gateway, Device device, Register register, M var1, Object value);
}