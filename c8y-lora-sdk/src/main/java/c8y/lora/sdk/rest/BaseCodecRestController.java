package c8y.lora.sdk.rest;

import c8y.lora.sdk.service.BaseDeviceCodecService;
import com.cumulocity.lpwan.codec.model.Decode;
import com.cumulocity.lpwan.codec.model.Encode;
import com.cumulocity.lpwan.devicetype.model.DecodedDataMapping;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BaseCodecRestController {

    @Autowired
    BaseDeviceCodecService codecService;

    @PostMapping(value = "/decode", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DecodedDataMapping decode(@RequestBody Decode decode) {
        return codecService.decode(decode);
    }

    @PostMapping(value = "/encode", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String encode(@RequestBody Encode encode) {
        return codecService.encode(encode);
    }

    @GetMapping(value = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getModels() {
        return codecService.getModels();
    }

    @GetMapping(value = "/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getMetaData() {
        return codecService.getMetData();
    }
}
