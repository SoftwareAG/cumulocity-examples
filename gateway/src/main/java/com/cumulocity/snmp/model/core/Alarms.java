package com.cumulocity.snmp.model.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Alarms {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alarm {
        private GId source;
        private AlarmRepresentation alarm;
    }

}
