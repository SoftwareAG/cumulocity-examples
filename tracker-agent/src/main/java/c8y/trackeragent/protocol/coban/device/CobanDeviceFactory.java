package c8y.trackeragent.protocol.coban.device;

import java.util.Map;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class CobanDeviceFactory {

    private static final String LOCATION_REPORT_INTERVAL = "locationReportInterval";
    private static final String DEFAULT_LOCATION_REPORT_INTERVAL = "03m";

    private final Map<String, Object> config;
    
    @SuppressWarnings("unchecked")
    public CobanDeviceFactory(ManagedObjectRepresentation device) {
        this.config = (Map<String, Object>) device.get("config");
    }

    public CobanDevice create() {
        CobanDevice result = new CobanDevice();
        result.setLocationReportInterval(getValue(LOCATION_REPORT_INTERVAL, DEFAULT_LOCATION_REPORT_INTERVAL));
        return result;
    }

    @SuppressWarnings("unchecked")
    private <V> V getValue(String key, V defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        V result = (V) config.get(key);
        return result == null ? defaultValue : result;
    }
}
