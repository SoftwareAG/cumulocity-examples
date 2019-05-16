package com.cumulocity.snmp.model.gateway.device;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.core.PersistableType;
import com.cumulocity.snmp.model.core.IdProvider;
import com.cumulocity.snmp.model.core.TenantProvider;
import com.google.common.base.Function;
import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.experimental.Wither;

@Data
@Wither
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@PersistableType(value = "Device", runWithinContext = TenantProvider.class, inMemory = true)
public class Device implements IdProvider {
    public static final String c8y_SNMPDevice = "c8y_SNMPDevice";

    private GId id;
    private String ipAddress;
    private GId deviceType;
    private int port;
    private int snmpVersion;
    private String username;
    private int securityLevel;
    private int authProtocol;
    private String authProtocolPassword;
    private int privacyProtocol;
    private String privacyProtocolPassword;
    private String engineId;

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
