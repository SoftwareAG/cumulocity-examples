package c8y.trackeragent.protocol.rfv16.parser;

import static java.util.Arrays.asList;

import java.util.List;

import c8y.ArmAlarm;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class ArmAlarmWrapper {

    private final ArmAlarm armAlarm;

    public ArmAlarmWrapper(ArmAlarm armAlarm) {
        this.armAlarm = armAlarm;
    }

    public Boolean getVibration() {
        return asBool("vibration");
    }

    public Boolean getNoise() {
        return asBool("noise");
    }

    public Boolean getDoor() {
        return asBool("door");
    }

    public Boolean getSos() {
        return asBool("sos");
    }

    private String getVibrationFlag() {
        return asFlag(getVibration(), "11", "01");
    }

    private String getNoiseFlag() {
        return asFlag(getNoise(), "12", "02");
    }

    private String getDoorFlag() {
        return asFlag(getDoor(), "13", "03");
    }

    private String getSosFlag() {
        return asFlag(getSos(), "14", "04");
    }

    public Iterable<String> getAllFlags() {
        List<String> result = asList(getVibrationFlag(), getNoiseFlag(), getDoorFlag(), getSosFlag());
        return Iterables.filter(result, Predicates.notNull());
    }

    private String asFlag(Boolean val, String valForTrue, String valForFalse) {
        if (val == null) {
            return null;
        }
        return val ? valForTrue : valForFalse;
    }

    private Boolean asBool(String propertyName) {
        Object val = armAlarm.getProperty(propertyName);
        if (val == null) {
            return null;
        }
        return Boolean.TRUE.equals(val);

    }

}
