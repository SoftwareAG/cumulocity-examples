package lora.codec.lansitec.encoder;

import com.cumulocity.microservice.customencoders.api.exception.EncoderServiceException;
import com.cumulocity.microservice.customencoders.api.model.EncoderInputData;
import com.cumulocity.microservice.customencoders.api.model.EncoderResult;
import com.cumulocity.microservice.customencoders.api.service.EncoderService;
import com.cumulocity.microservice.lpwan.codec.encoder.model.LpwanEncoderInputData;
import com.cumulocity.microservice.lpwan.codec.encoder.model.LpwanEncoderResult;
import com.cumulocity.model.idtype.GId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Component
public class LansitecEncoder implements EncoderService {
    public static final String SET_CONFIG = "set config";
    public static final String DEVICE_REQUEST = "device request";
    public static final String REGISTER_REQUEST = "register request";
    public static final String POSITION_REQUEST = "position request";

    @Override
    public EncoderResult encode(EncoderInputData encoderInputData) throws EncoderServiceException {
        LpwanEncoderInputData lpwanEncoderInputData = new LpwanEncoderInputData(GId.asGId(encoderInputData.getSourceDeviceId()),
                encoderInputData.getCommandName(),
                encoderInputData.getCommandData(),
                encoderInputData.getArgs());

        LpwanEncoderResult encoderResult = null;
        if (lpwanEncoderInputData.getSourceDeviceInfo().getManufacturer().equalsIgnoreCase("Lansitec") && lpwanEncoderInputData.getSourceDeviceInfo().getModel().equals("Asset Tracker")) {
            ObjectMapper mapper = new ObjectMapper();
            String payload = null;
            try {
                if (lpwanEncoderInputData.getCommandName().equals(POSITION_REQUEST)) {
                    payload = "A1FF";
                } else if (lpwanEncoderInputData.getCommandName().equals(REGISTER_REQUEST)) {
                    payload = "A2FF";
                } else if (lpwanEncoderInputData.getCommandName().equals(DEVICE_REQUEST)) {
                    payload = "A3FF";
                } else if (lpwanEncoderInputData.getCommandName().equals(SET_CONFIG)) {
                    JsonNode params = mapper.readTree(lpwanEncoderInputData.getCommandData());
                    ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
                    byte breakpoint = params.get("breakpoint").asBoolean() ? (byte) 8 : 0;
                    byte selfadapt = params.get("selfadapt").asBoolean() ? (byte) 4 : 0;
                    byte oneoff = params.get("oneoff").asBoolean() ? (byte) 2 : 0;
                    byte alreport = params.get("alreport").asBoolean() ? (byte) 1 : 0;
                    buffer.put((byte) ((byte) 0x90 | (byte) breakpoint | (byte) selfadapt | (byte) oneoff | (byte) alreport));
                    buffer.putShort((short) params.get("pos").asInt());
                    buffer.put((byte) params.get("hb").asInt());
                    payload = BaseEncoding.base16().encode(buffer.array());
                }

                encoderResult = new LpwanEncoderResult(payload, 20);
                encoderResult.setSuccess(true);
                encoderResult.setMessage("Successfully Encoded the payload");
            } catch (IOException e) {
                e.printStackTrace();
                encoderResult = new LpwanEncoderResult();
                encoderResult.setSuccess(false);
                encoderResult.setMessage("Encoding Payload Failed");
            }
        }
        return encoderResult;
    }
}
