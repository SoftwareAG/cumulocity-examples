package c8y.example.bytesequencedecoder;

import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementDto;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementValueDto;
import com.cumulocity.microservice.customdecoders.api.service.DecoderService;
import com.cumulocity.microservice.customdecoders.api.util.DecoderUtils;
import com.cumulocity.model.idtype.GId;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ByteSequenceDecoderService implements DecoderService {

    /**
     * Assumes the payload is a hex string of bytes; converts every byte into a measurement
     * @param payloadToDecode
     * @param sourceDeviceId
     * @param inputArguments
     * @return
     * @throws DecoderServiceException
     */

    @Override
    public DecoderResult decode(String payloadToDecode, GId sourceDeviceId, Map<String, String> inputArguments) throws DecoderServiceException {

        byte[] decodedBytes = DecoderUtils.hexStringToByteArray(payloadToDecode);
        DecoderResult decoderResult = new DecoderResult();

        List<MeasurementValueDto> measurementValueDtos = new ArrayList<>();
        int byteIndex=0;

        for (byte valueByte: decodedBytes) {

            MeasurementValueDto valueDto = new MeasurementValueDto();
            valueDto.setValue(new BigDecimal(valueByte));
            valueDto.setSeriesName("Byte "+byteIndex);
            valueDto.setUnit("unknown");
            measurementValueDtos.add(valueDto);
        }

        MeasurementDto measurementDto = new MeasurementDto();
        measurementDto.setType("c8y_example_lwm2m_decoder_binaryValues");
        measurementDto.setTime(new DateTime());
        measurementDto.setValues(measurementValueDtos);

        decoderResult.addMeasurement(measurementDto);

        return decoderResult;

    }
}
