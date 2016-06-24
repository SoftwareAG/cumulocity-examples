package c8y.trackeragent.protocol.coban.device;

import java.util.Map;

import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.protocol.coban.CobanConstants;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.google.common.base.Strings;

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
        Integer defaultLocationReportInterval = trackerConfig.getCobanLocationReportTimeInterval();
        Integer locationReportInterval = getValue(CobanConstants.DEVICE_CONFIG_KEY_LOCATION_REPORT_TIME_INTERVAL, defaultLocationReportInterval);
        result.setLocationReportInterval(formatLocationReportInterval(locationReportInterval));
        return result;
    }

    public static String formatLocationReportInterval(Integer locationReportInterval) {
        String unit = "s";
        if (locationReportInterval >= 60) {
            locationReportInterval = locationReportInterval / 60;
            unit = "m";
        }
        return Strings.padStart(locationReportInterval.toString(), 2, '0') + unit;
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
