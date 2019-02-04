package com.cumulocity.snmp.model.type;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.core.PersistableType;
import com.cumulocity.snmp.model.core.IdProvider;
import com.cumulocity.snmp.model.core.TenantProvider;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Wither;

import javax.annotation.Nullable;
import java.util.List;

@Data
@Builder
@Wither
@AllArgsConstructor
@NoArgsConstructor
@PersistableType(value = "DeviceType", runWithinContext = TenantProvider.class, inMemory = true)
public class DeviceType implements IdProvider {

    public static final String FIELDBUS_TYPE = "fieldbusType";
    public static final String SNMP_TYPE = "snmp";

    private GId id;

    private String name;

    private String type;

    @Nullable
    @JsonProperty(FIELDBUS_TYPE)
    private String fieldbusType;

    @Nullable
    @Singular
    @JsonProperty("c8y_Registers")
    private List<Register> registers;

    @Nullable
    @JsonProperty("c8y_useServerTime")
    private Boolean useServerTime;
}
