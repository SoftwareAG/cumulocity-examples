package lora.codec.lansitec;

import com.cumulocity.microservice.lpwan.codec.Codec;
import com.cumulocity.microservice.lpwan.codec.model.DeviceInfo;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LansitecCodec implements Codec {

    /**
     * This method should populate a set of unique devices identified by their manufacturer and model.
     * @return Set<DeviceInfo>: A set of unique devices identified by their manufacturer and model.
     */
    public Set<DeviceInfo> supportsDevices() {

        // The manufacturer "LANSITEC" has 2 different devices with model "Outdoor Asset Tracker" and "Temperature Sensor"
        DeviceInfo deviceInfo_Lansitec_Asset_Tracker = new DeviceInfo("LANSITEC", "Asset Tracker");
        DeviceInfo deviceInfo_Lansitec_Temperature_Sensor = new DeviceInfo("LANSITEC", "Temperature Sensor");

        return Stream.of(deviceInfo_Lansitec_Asset_Tracker, deviceInfo_Lansitec_Temperature_Sensor).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     *
     * @return String : The context path of the codec microservice
     */
    public String getMicroserviceContextPath() {
        return "lora-codec-lansitec";
    }
}
