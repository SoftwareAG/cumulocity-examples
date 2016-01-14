package c8y.trackeragent.protocol.rfv16.device;

import java.util.Map;

import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class RFV16DeviceFactory {

    private final Map<String, Object> config;
    private final TrackerConfiguration trackerConfig;
    
    @SuppressWarnings("unchecked")
    public RFV16DeviceFactory(TrackerConfiguration trackerConfig, ManagedObjectRepresentation device) {
        this.trackerConfig = trackerConfig;
        this.config = (Map<String, Object>) device.get(RFV16Constants.DEVICE_CONFIG_FRAGMENT);
    }

    public RFV16Device create() {
        RFV16Device result = new RFV16Device();
        String defaultLocationReportInterval = trackerConfig.getCobanLocationReportTimeInterval();
        String locationReportInterval = getValue(RFV16Constants.DEVICE_CONFIG_KEY_LOCATION_REPORT_TIME_INTERVAL, defaultLocationReportInterval);
        result.setLocationReportInterval(locationReportInterval);
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
