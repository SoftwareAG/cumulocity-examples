package com.cumulocity.snmp.model.gateway.type.core;

import com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping;
import com.cumulocity.snmp.model.gateway.type.mapping.EventMapping;
import com.cumulocity.snmp.model.gateway.type.mapping.ManagedObjectMapping;
import com.cumulocity.snmp.model.gateway.type.mapping.MeasurementMapping;
import lombok.*;
import org.snmp4j.smi.Variable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = "name")
public class Register{
    public static final String c8y_RegisterStatus = "c8y_RegisterStatus";

    @NotNull
    private String name;

    @NotNull
    private String oid;
    private String description;

    @NotNull
    private String parentOid;

    private List<String> childOids;

    private String unit;

    @Nullable
    private AlarmMapping alarmMapping;

    @Nullable
    private EventMapping eventMapping;

    @Nullable
    private MeasurementMapping measurementMapping;

    @Nullable
    private ManagedObjectMapping managedObjectMapping;


    public Iterable<Mapping> mappings() {
        final LinkedList<Mapping> result = new LinkedList<>();
        if (alarmMapping != null) {
            result.add(alarmMapping);
        }
        if (eventMapping != null) {
            result.add(eventMapping);
        }
        if (measurementMapping != null) {
            result.add(measurementMapping);
        }
        if (managedObjectMapping != null) {
            result.add(managedObjectMapping);
        }
        return result;
    }

    public Object convert(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Variable) {
            return new Double(((Variable) object).toInt());
        }
        if (object instanceof Boolean) {
            if ((Boolean) object)  {
                return 1;
            } else {
                return 0;
            }
        }
        return object;
    }
}
