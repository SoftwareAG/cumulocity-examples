package com.cumulocity.snmp.model.gateway.device;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.core.PersistableType;
import com.cumulocity.snmp.model.core.IdProvider;
import com.cumulocity.snmp.model.core.TenantProvider;
import com.google.common.base.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.experimental.Wither;

@Data
@Wither
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PersistableType(value = "Device", runWithinContext = TenantProvider.class, inMemory = true)
public class Device implements IdProvider {
    public static final String c8y_SNMPDevice = "c8y_SNMPDevice";

    private GId id;
    private String ipAddress;
    private GId deviceType;
    private int port;
    private int snmpVersion;

    @UtilityClass
    public static class Method {
        public static Function<Device, String> getId() {
            return new Function<Device, String>() {
                @Override
                public String apply(final Device device) {
                    return device.getId().getValue();
                }
            };
        }

        public static Function<GId, String> gidGetValue() {
            return new Function<GId, String>() {
                @Override
                public String apply(final GId id) {
                    return id.getValue();
                }
            };
        }
    }
}
