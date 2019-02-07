package c8y.example.bytesequencedecoder;

import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementDto;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementValueDto;
import com.cumulocity.microservice.customdecoders.api.service.DecoderService;
import com.cumulocity.microservice.customdecoders.api.util.DecoderUtils;
import com.cumulocity.model.idtype.GId;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
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

        log.debug("Decoding payload {}. Converting hex string into values", payloadToDecode);

        byte[] decodedBytes = DecoderUtils.hexStringToByteArray(payloadToDecode);

        log.debug("Byte values: {}",decodedBytes);

        DecoderResult decoderResult = new DecoderResult();

        List<MeasurementValueDto> measurementValueDtos = new ArrayList<>();
        int byteIndex=0;

        for (byte valueByte: decodedBytes) {
            log.debug("Creating Measurement for byte {}, value {}", byteIndex,valueByte);
            MeasurementValueDto valueDto = new MeasurementValueDto();
            valueDto.setValue(new BigDecimal(valueByte));
            valueDto.setSeriesName("byte "+byteIndex);
            valueDto.setUnit("unknown");
            measurementValueDtos.add(valueDto);

            MeasurementDto measurementDto = new MeasurementDto();
            measurementDto.setType("c8y_example_lwm2m_decoder_binaryValues_byteIndex_"+byteIndex);
            measurementDto.setTime(new DateTime());
            measurementDto.setValues(measurementValueDtos);
            measurementDto.setSeries("binaryValueSeries");
            decoderResult.addMeasurement(measurementDto);

            byteIndex++;
        }


        log.debug("Finished decoding byte values");
        return decoderResult;

    }
}
