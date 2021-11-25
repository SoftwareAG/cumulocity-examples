package c8y.example.service;

import com.cumulocity.lpwan.codec.CodecMicroservice;
import com.cumulocity.lpwan.codec.model.DeviceInfo;
import com.cumulocity.lpwan.codec.model.DeviceTypeEnum;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeviceCodecMicroservice extends CodecMicroservice {

    public Set<DeviceInfo> supportsDevices() {
        DeviceInfo deviceInfo_X = new DeviceInfo("Dummy_Manufacturer_X", "Dummy_Model_X", DeviceTypeEnum.LORA);
        DeviceInfo deviceInfo_Y = new DeviceInfo("Dummy_Manufacturer_Y", "Dummy_Model_Y", DeviceTypeEnum.SIGFOX);
        return Stream.of(deviceInfo_X,deviceInfo_Y).collect(Collectors.toCollection(HashSet::new));
    }

    public String getMicroserviceContextPath(){
        return "lpwancodecmicroservice";
    }
}
