package com.cumulocity.lora.codec.twtg.neon.decoder;

import com.cumulocity.lora.codec.twtg.neon.Application;
import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.model.idtype.GId;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableWebMvc
class NeonDecoderTest {

    @Autowired
    private NeonDecoder decoder;

    @Test
    void doDecode() {
        Map<String, String> decoderArgs = new HashMap<>();
        decoderArgs.put("deviceManufacturer", "TWTG");
        decoderArgs.put("deviceModel", "Neon Temperature Sensor");
        decoderArgs.put("sourceDeviceEui", "EUIID-12345");
        decoderArgs.put("fport", "20");

        try {
            // HEADER=23
            //      Protocol=2
            //      Message_Type=3 (means application_event)
            // Trigger=00 (means Timer)
            // Temperature=8813;1027;4c1d
            //      min= 50 C which means 5000 and int value in hex = 1388 but represented in reverse so, 8813
            //      max= 100 C which means 10000 and int value in hex = 2710 but represented in reverse so, 1027
            //      min= 50 C which means 5000 and int value in hex = 1d4c but represented in reverse so, 4c1d
            // Condition=00 (means none of the conditions 0 to 3)

            DecoderResult decoderResult = decoder.decode("2300881310274c1d00", GId.asGId(12345), decoderArgs);

            assertEquals(50, decoderResult.getMeasurements().get(0).getValues().get(0).getValue().intValue());
            assertEquals(100, decoderResult.getMeasurements().get(0).getValues().get(1).getValue().intValue());
            assertEquals(75, decoderResult.getMeasurements().get(0).getValues().get(2).getValue().intValue());
        } catch (DecoderServiceException e) {
            fail(e);
        }
    }
}