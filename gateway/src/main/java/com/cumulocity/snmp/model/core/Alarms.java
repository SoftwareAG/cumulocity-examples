package com.cumulocity.snmp.model.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.List;

import static com.cumulocity.snmp.model.core.Alarms.Alarm.Method.getAlarm;
import static com.google.common.collect.FluentIterable.from;

@Data
public class Alarms {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alarm {
        private ConfigEventType type;
        private GId source;
        private AlarmRepresentation alarm;

        @UtilityClass
        public static class Method {
            public static Predicate<Alarm> sourceEquals(final GId source) {
                return new Predicate<Alarm>() {
                    public boolean apply(Alarm value) {
                        return value.getSource().equals(source);
                    }
                };
            }

            public static Predicate<Alarm> typeEquals(final ConfigEventType type) {
                return new Predicate<Alarm>() {
                    public boolean apply(Alarm value) {
                        return type == null || value.getType() == null || value.getType().equals(type);
                    }
                };
            }

            public static Function<Alarm, AlarmRepresentation> getAlarm() {
                return new Function<Alarm, AlarmRepresentation>() {
                    public AlarmRepresentation apply(Alarm value) {
                        return value.getAlarm();
                    }
                };
            }

            public static FluentIterable<Alarm> getBySourceAndType(List<Alarm> alarms, GId source, ConfigEventType type) {
                return from(alarms).filter(sourceEquals(source)).filter(typeEquals(type));
            }
        }
    }

    private List<Alarm> active = Lists.newArrayList();

    public boolean existsBySourceAndType(final GId source, final ConfigEventType type) {
        return !Alarm.Method.getBySourceAndType(active, source, type).isEmpty();
    }

    public void add(ConfigEventType type, AlarmRepresentation alarm) {
        getActive().add(new Alarm(type, alarm.getSource().getId(), alarm));
    }

    public List<AlarmRepresentation> getBySourceAndType(final GId source, final ConfigEventType type) {
        return Alarm.Method.getBySourceAndType(getActive(), source, type).transform(getAlarm()).toList();
    }

    public void clearBySourceAndType(final GId source, final ConfigEventType type) {
        for (final Alarm value : Alarm.Method.getBySourceAndType(getActive(), source, type).toList()) {
            getActive().remove(value);
        }
    }

    public List<Alarm> getActive() {
        if (active == null) {
            active = Lists.newArrayList();
        }
        return active;
    }
}
