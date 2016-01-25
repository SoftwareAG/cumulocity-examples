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

    private String getAll() {
	return boolAsSymbol("all", "10", "00");
    }

    private  String getVibration() {
	return boolAsSymbol("vibration", "11", "01");
    }

    private String getNoise() {
	return boolAsSymbol("noise", "12", "02");
    }

    private String getDoor() {
	return boolAsSymbol("door", "13", "03");
    }

    private String getSos() {
	return boolAsSymbol("sos", "14", "04");
    }
    
    public Iterable<String> getAllValues() {
	List<String> result = asList(getAll(), getVibration(), getNoise(), getDoor(), getSos());
	return Iterables.filter(result, Predicates.notNull());
    }

    private String boolAsSymbol(String propertyName, String valForTrue, String valForFalse) {
	Object val = armAlarm.getProperty(propertyName);
	if (val == null) {
	    return null;
	}
	return Boolean.TRUE.equals(val) ? valForTrue : valForFalse;
    }

}
