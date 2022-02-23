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
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.svenson.JSON;
import org.svenson.JSONParser;
import org.svenson.tokenize.InputStreamSource;

import java.io.IOException;
import java.io.OutputStreamWriter;

@Slf4j
public class JsonDecoderMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final JSONParser jsonParser;

    public JsonDecoderMessageConverter() {
        super();
        log.info("Setting up Svenson JSON Parser and Generator with DateTime Conversion");
        jsonParser = JSONParser.defaultJSONParser();
        JSON jsonGenerator = JSONBase.getJSONGenerator();

        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        jsonParser.registerTypeConversion(DateTime.class, dateTimeConverter);
        jsonGenerator.registerTypeConversion(DateTime.class, dateTimeConverter);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return JSONBase.getJSONParser().parse(clazz, new InputStreamSource(inputMessage.getBody(), true));
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody())) {
            JSONBase.getJSONGenerator().writeJSONToWriter(object, writer);
            writer.flush();
        }
    }
}
