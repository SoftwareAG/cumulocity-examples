package com.cumulocity.snmp.model.gateway.type.mapping;

import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder(builderMethodName = "alarmMapping")
@AllArgsConstructor
@NoArgsConstructor
public class AlarmMapping implements Mapping {
    public static final String c8y_ValidationError = "c8y_ValidationError";
    public static final String c8y_TRAPReceivedFromUnknownDevice = "c8y_TRAPReceivedFromUnknownDevice";
    public static final String c8y_DeviceNotResponding = "c8y_DeviceNotResponding";
    public static final String c8y_DeviceSnmpNotEnabled = "c8y_DeviceSnmpNotEnabled";

    @NotNull
    private String type;
    private String text;
    private AlarmSeverity severity;
    private String status;
}
