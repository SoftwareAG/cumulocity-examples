package com.cumulocity.snmp.model.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.core.PersistableType;
import com.cumulocity.snmp.annotation.model.ExtensibleRepresentationView;
import com.cumulocity.snmp.model.core.Alarms;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.core.IdProvider;
import com.cumulocity.snmp.model.core.TenantProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Wither;

import javax.annotation.Nullable;
import java.util.List;

@Data
@Builder(builderMethodName = "gateway")
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "name")
@EqualsAndHashCode(of = {"tenant", "name", "password", "id", "currentDeviceIds"})
@PersistableType(value = "Gateway")
@ExtensibleRepresentationView(fragment = Gateway.c8y_SNMPGateway, type = Gateway.TYPE)
public class Gateway implements IdProvider, TenantProvider, Credentials {

    public static final String TYPE = "c8y_SNMP";
    public static final String c8y_SNMPGateway = "c8y_SNMPGateway";
    public static final String c8y_SetRegister = "c8y_SetRegister";

    @Wither
    @Nullable
    private String tenant;

    @Wither
    @Nullable
    private String name;

    @Wither
    @Nullable
    private String password;

    @Wither
    @Nullable
    private GId id;

    @Wither
    private Alarms alarms = new Alarms();

    @Wither
    @Nullable
    private List<GId> currentDeviceIds;

    @Nullable
    @JsonProperty
    private volatile int numberOfRetries = 0;

    public Alarms getAlarms() {
        if (alarms == null) {
            alarms = new Alarms();
        }
        return alarms;
    }

    @JsonIgnore
    public int increaseNumberOfRetries() {
        return ++numberOfRetries;
    }
}

