package c8y.example.bytesequencedecoder;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DecoderInputData;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.model.idtype.GId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@MicroserviceApplication
@RequestMapping(value = "/decode")
public class ByteSequenceDecoder {


    @Autowired
    ByteSequenceDecoderService byteSequenceDecoderService;

    public static void main(String[] args) {
        SpringApplication.run(ByteSequenceDecoder.class,args);
    }


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DecoderResult decodeWithJSONInput(@RequestBody DecoderInputData inputData) throws DecoderServiceException, IOException {
        return byteSequenceDecoderService.decode(inputData.getValue(), GId.asGId(inputData.getSourceDeviceId()), inputData.getArgs());
    }

}
