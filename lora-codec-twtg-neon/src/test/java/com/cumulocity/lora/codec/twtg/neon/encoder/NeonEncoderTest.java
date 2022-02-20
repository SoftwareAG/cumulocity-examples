/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2022 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.lora.codec.twtg.neon.encoder;

import com.cumulocity.lora.codec.twtg.neon.Application;
import com.cumulocity.microservice.customencoders.api.exception.EncoderServiceException;
import com.cumulocity.microservice.customencoders.api.model.EncoderResult;
import com.cumulocity.microservice.lpwan.codec.encoder.model.LpwanEncoderInputData;
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
class NeonEncoderTest {

    @Autowired
    private NeonEncoder encoder;

    @Test
    void doEncode_application_configuration_message() {
        // Application Configuration message created using the configuration generator at https://neon-configurator.twtg.io/neon/ts/v2/
        // This message is supposed to be encoded into a base16/hex string "26010000100001401f000260f00003e803000418fc00a955"
        //{
        //  "header": {
        //    "message_type": "application_configuration",
        //    "protocol_version": 2
        //  },
        //  "device_type": "ts",
        //  "temperature_measurement_interval__seconds": 900,
        //  "periodic_event_message_interval": 16,
        //  "events": [
        //    {
        //      "mode": "above",
        //      "threshold_temperature": 100,
        //      "measurements_window": 1
        //    },
        //    {
        //      "mode": "below",
        //      "threshold_temperature": -100,
        //      "measurements_window": 1
        //    },
        //    {
        //      "mode": "increasing",
        //      "threshold_temperature": 50,
        //      "measurements_window": 1
        //    },
        //    {
        //      "mode": "decreasing",
        //      "threshold_temperature": -50,
        //      "measurements_window": 1
        //    }
        //  ]
        //}
        String command = "{\n" +
                "  \"header\": {\n" +
                "    \"message_type\": \"application_configuration\",\n" +
                "    \"protocol_version\": 2\n" +
                "  },\n" +
                "  \"device_type\": \"ts\",\n" +
                "  \"temperature_measurement_interval__seconds\": 900,\n" +
                "  \"periodic_event_message_interval\": 16,\n" +
                "  \"events\": [\n" +
                "    {\n" +
                "      \"mode\": \"above\",\n" +
                "      \"threshold_temperature\": 80,\n" +
                "      \"measurements_window\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"below\",\n" +
                "      \"threshold_temperature\": -40,\n" +
                "      \"measurements_window\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"increasing\",\n" +
                "      \"threshold_temperature\": 10,\n" +
                "      \"measurements_window\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"mode\": \"decreasing\",\n" +
                "      \"threshold_temperature\": -10,\n" +
                "      \"measurements_window\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        LpwanEncoderInputData lpwanEncoderInputData = new LpwanEncoderInputData(GId.asGId("12345"), "Configure Application", command, getEncoderArguments());
        try {
            EncoderResult encoderResult = encoder.encode(lpwanEncoderInputData);

            assertEquals("26010000100001401f000260f00003e803000418fc00a955", encoderResult.getEncodedCommand());
        } catch (EncoderServiceException e) {
            fail(e);
        }
    }

    @Test
    void doEncode_device_configuration_message() {
        // Device Configuration message created using the configuration generator at https://neon-configurator.twtg.io/neon/ts/v2/
        // This message is supposed to be encoded into a base16/hex string "250003023ca005010201ff0000000000000000009802"
        //{
        //	"header": {
        //		"message_type": "device_configuration",
        //		"protocol_version": 2
        //	},
        //	"switch_mask": {
        //		"enable_confirmed_changed_message": true
        //	},
        //	"communication_max_retries": 3,
        //	"unconfirmed_repeat": 2,
        //	"periodic_message_random_delay_seconds": 60,
        //	"status_message_interval_seconds": 86400,
        //	"status_message_confirmed_interval": 1,
        //	"lora_failure_holdoff_count": 2,
        //	"lora_system_recover_count": 1,
        //	"lorawan_fsb_mask": [
        //		"0x00FF",
        //		"0x0000",
        //		"0x0000",
        //		"0x0000",
        //		"0x0000"
        //	]
        //}
        String command = "{\n" +
                "\t\"header\": {\n" +
                "\t\t\"message_type\": \"device_configuration\",\n" +
                "\t\t\"protocol_version\": 2\n" +
                "\t},\n" +
                "\t\"switch_mask\": {\n" +
                "\t\t\"enable_confirmed_changed_message\": true\n" +
                "\t},\n" +
                "\t\"communication_max_retries\": 3,\n" +
                "\t\"unconfirmed_repeat\": 2,\n" +
                "\t\"periodic_message_random_delay_seconds\": 60,\n" +
                "\t\"status_message_interval_seconds\": 86400,\n" +
                "\t\"status_message_confirmed_interval\": 1,\n" +
                "\t\"lora_failure_holdoff_count\": 2,\n" +
                "\t\"lora_system_recover_count\": 1,\n" +
                "\t\"lorawan_fsb_mask\": [\n" +
                "\t\t\"0x00FF\",\n" +
                "\t\t\"0x0000\",\n" +
                "\t\t\"0x0000\",\n" +
                "\t\t\"0x0000\",\n" +
                "\t\t\"0x0000\"\n" +
                "\t]\n" +
                "}";

        LpwanEncoderInputData lpwanEncoderInputData = new LpwanEncoderInputData(GId.asGId("12345"), "Configure Device", command, getEncoderArguments());
        try {
            EncoderResult encoderResult = encoder.encode(lpwanEncoderInputData);

            assertEquals("250003023ca005010201ff0000000000000000009802", encoderResult.getEncodedCommand());
        } catch (EncoderServiceException e) {
            fail(e);
        }
    }

    private Map<String, String> getEncoderArguments() {
        Map<String, String> encoderArgs = new HashMap<>();
        encoderArgs.put("deviceManufacturer", "TWTG");
        encoderArgs.put("deviceModel", "Neon Temperature Sensor");
        encoderArgs.put("sourceDeviceEui", "EUIID-12345");
        return encoderArgs;
    }
}