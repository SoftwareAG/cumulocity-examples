/*
 * Copyright © 2019 Software AG, Darmstadt, Germany and/or its licensors
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package c8y.example.decoders.json.configuration;

import com.cumulocity.model.DateTimeConverter;
import com.cumulocity.model.JSONBase;
import com.cumulocity.sdk.client.rest.providers.SvensonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.svenson.JSON;
import org.svenson.JSONParser;

@Slf4j
public class JsonDecoderMessageConverter extends SvensonHttpMessageConverter {

    private final JSONParser jsonParser;
    private final MappingJackson2HttpMessageConverter jacksonConverter;

    public JsonDecoderMessageConverter() {
        super();
        log.info("Setting up Svenson JSON Parser and Generator with DateTime Conversion");
        jsonParser = JSONParser.defaultJSONParser();
        JSON jsonGenerator = JSONBase.getJSONGenerator();

        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        jacksonConverter = new MappingJackson2HttpMessageConverter();
        jsonParser.registerTypeConversion(DateTime.class, dateTimeConverter);
        jsonGenerator.registerTypeConversion(DateTime.class, dateTimeConverter);
    }
}
