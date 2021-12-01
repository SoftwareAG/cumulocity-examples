package c8y.example.service;

import com.cumulocity.lpwan.codec.CodecMicroservice;
import com.cumulocity.lpwan.codec.model.DeviceInfo;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeviceCodecMicroservice extends CodecMicroservice {

    /**
     * This method should populate a set of unique devices identified by their manufacturer and model.
     *
     * @return Set<DeviceInfo>: A set of unique devices identified by their manufacturer and model.
     */
    public Set<DeviceInfo> supportsDevices() {

        // The manufacturer "NKE" has 2 different devices with model "50-70-043" and "50-70-164"
        DeviceInfo deviceInfo_NKE_50_70_043 = new DeviceInfo("NKE", "50-70-043");
        DeviceInfo deviceInfo_NKE_50_70_164 = new DeviceInfo("NKE", "50-70-164");

        // The manufacturer "LANSITEC" has 2 different devices with model "Outdoor Asset Tracker" and "Temperature Sensor"
        DeviceInfo deviceInfo_Lansitec_Outdoor_Asset_Tracker = new DeviceInfo("LANSITEC", "Outdoor Asset Tracker");
        DeviceInfo deviceInfo_Lansitec_Temperature_Sensor = new DeviceInfo("LANSITEC", "Temperature Sensor");

        return Stream.of(deviceInfo_NKE_50_70_043, deviceInfo_NKE_50_70_164, deviceInfo_Lansitec_Outdoor_Asset_Tracker, deviceInfo_Lansitec_Temperature_Sensor).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     *
     * @return String : The context path of the codec microservice
     */
    public String getMicroserviceContextPath() {
        return "Nke-Lansitec-Decoder-Sample";
    }
}
