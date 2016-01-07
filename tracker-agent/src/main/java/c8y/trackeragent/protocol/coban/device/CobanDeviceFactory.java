package c8y.trackeragent.protocol.coban.device;

import java.util.Map;

import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class CobanDeviceFactory {

    private final Map<String, Object> config;
    private final TrackerConfiguration trackerConfig;
    
    @SuppressWarnings("unchecked")
    public CobanDeviceFactory(TrackerConfiguration trackerConfig, ManagedObjectRepresentation device) {
        this.trackerConfig = trackerConfig;
        this.config = (Map<String, Object>) device.get(CobanConstants.DEVICE_CONFIG_FRAGMENT);
    }

    public CobanDevice create() {
        CobanDevice result = new CobanDevice();
        String defaultLocationReportInterval = trackerConfig.getCobanLocationReportTimeInterval();
        result.setLocationReportInterval(getValue(CobanConstants.DEVICE_CONFIG_KEY_LOCATION_REPORT_TIME_INTERVAL, defaultLocationReportInterval));
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
