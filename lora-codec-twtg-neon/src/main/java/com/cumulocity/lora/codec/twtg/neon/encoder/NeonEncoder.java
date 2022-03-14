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

import com.cumulocity.lora.codec.twtg.neon.javascript.engine.JavaScriptEngine;
import com.cumulocity.microservice.customencoders.api.exception.EncoderServiceException;
import com.cumulocity.microservice.customencoders.api.model.EncoderInputData;
import com.cumulocity.microservice.customencoders.api.model.EncoderResult;
import com.cumulocity.microservice.customencoders.api.service.EncoderService;
import com.cumulocity.microservice.lpwan.codec.encoder.model.LpwanEncoderInputData;
import com.cumulocity.microservice.lpwan.codec.encoder.model.LpwanEncoderResult;
import com.cumulocity.model.idtype.GId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NeonEncoder implements EncoderService {
    private final Logger logger = LoggerFactory.getLogger(NeonEncoder.class);

    @Autowired
    private JavaScriptEngine graalJavaScriptEngineProxy;

    @Override
    public EncoderResult encode(EncoderInputData encoderInputData) throws EncoderServiceException {
        LpwanEncoderInputData lpwanEncoderInputData = new LpwanEncoderInputData(GId.asGId(encoderInputData.getSourceDeviceId()),
                encoderInputData.getCommandName(),
                encoderInputData.getCommandData(),
                encoderInputData.getArgs());

        try {
            // TODO: Should be changed based on the device specification
            int fport = 20;

            // Encode the device payload by invoking the EncodeForGraaljs Javascript function,
            // a wrapper which in turn invokes the Encode() function from '/js/codec/encoder_ts_prot-2_doc-v2.2.1_rev-0'.
            //
            // This wrapper function (from '/js/codec/wrapper_functions.js') is created to overcome the
            // limitations of Graaljs with its handling of multilevel json objects and certain other data types.
            String encodedData = graalJavaScriptEngineProxy.invokeFunction("EncodeForGraaljs", String.class, fport, encoderInputData.getCommandData());

            return new LpwanEncoderResult(
                    encodedData, // Passing the encoded data returned by the the wrapper function as is, assuming that the Device expects a Base16 encoded string.
                    fport);
        } catch (Exception e) {
            logger.error("Encoding payload failed. Error: " + e.getMessage(), e);

            throw new EncoderServiceException(e, "Encoding payload failed. Error: " + e.getMessage(), new LpwanEncoderResult());
        }
    }
}
