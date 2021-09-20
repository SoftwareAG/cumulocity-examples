package c8y.lora.codec;

import com.cumulocity.lpwan.codec.model.Decode;
import com.cumulocity.lpwan.codec.model.Encode;
import com.cumulocity.lpwan.codec.rest.BaseCodecRestController;
import com.cumulocity.lpwan.devicetype.model.DecodedDataMapping;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GenericCodecController extends BaseCodecRestController {

    @Autowired
    GenericCodecService codecService;

    @Override
    public String encode(@RequestBody Encode encode) {
        return codecService.encode(encode);
    }

    @Override
    public DecodedDataMapping decode(@RequestBody Decode decode) {
        return codecService.decode(decode);
    }

    @Override
    public List<String> getModels() {
        return codecService.getModels();
    }

    @Override
    public JSONObject getMetaData() {
        return codecService.getMetData();
    }

    /*@PostMapping(value = "/decodeTest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DecodedDataMapping decode1(@RequestBody Decode decode){
        return codecService.decode(decode);
    }*/

    /*@GetMapping(value = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getModels() {
        return "Dummy Model";
    }*/
}
