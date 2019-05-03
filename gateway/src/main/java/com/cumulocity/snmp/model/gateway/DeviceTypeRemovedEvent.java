package com.cumulocity.snmp.model.gateway;

import com.cumulocity.model.idtype.GId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceTypeRemovedEvent {

    private GId deviceTypeId;
}
