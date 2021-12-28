package lora.codec.lansitec.encoder;

import com.cumulocity.microservice.customencoders.api.exception.EncoderServiceException;
import com.cumulocity.microservice.customencoders.api.model.EncoderInputData;
import com.cumulocity.microservice.customencoders.api.model.EncoderResult;
import com.cumulocity.microservice.customencoders.api.service.EncoderService;
import org.springframework.stereotype.Component;

@Component
public class LansitecEncoder implements EncoderService {
    @Override
    public EncoderResult encode(EncoderInputData encoderInputData) throws EncoderServiceException {
        return new EncoderResult();
    }
}
