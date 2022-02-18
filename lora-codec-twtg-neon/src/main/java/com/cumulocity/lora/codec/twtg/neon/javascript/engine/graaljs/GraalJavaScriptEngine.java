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

package com.cumulocity.lora.codec.twtg.neon.javascript.engine.graaljs;

import com.cumulocity.lora.codec.twtg.neon.javascript.engine.JavaScriptEngine;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Component(GraalJavaScriptEngine.GRAAL_JAVA_SCRIPT_ENGINE_BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GraalJavaScriptEngine implements JavaScriptEngine {
    public static final String GRAAL_JAVA_SCRIPT_ENGINE_BEAN_NAME = "graalJavaScriptEngineTarget";

    private static final String DECODER_TS_PROT_2_DOC_V_2_2_1_REV_0_JS = "/js/codec/decoder_ts_prot-2_doc-v2.2.1_rev-0.js";
    private static final String ENCODER_TS_PROT_2_DOC_V_2_2_1_REV_0_JS = "/js/codec/encoder_ts_prot-2_doc-v2.2.1_rev-0.js";

    private Invocable invocable;

    public GraalJavaScriptEngine() throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");

        try (BufferedReader decoderScriptReader = Files.newBufferedReader(Paths.get(this.getClass().getResource(DECODER_TS_PROT_2_DOC_V_2_2_1_REV_0_JS).toURI()))) {
            engine.eval(decoderScriptReader);
        }
        try (BufferedReader encoderScriptReader = Files.newBufferedReader(Paths.get(this.getClass().getResource(ENCODER_TS_PROT_2_DOC_V_2_2_1_REV_0_JS).toURI()))) {
            engine.eval(encoderScriptReader);

            // A wrapper for Encode function, so we can pass the Json object as a String and
            // also read the response as a String instead of a byte[] to overcome the limitations
            // of Graaljs with handling of multilevel json objects and certain other data types
            // function EncodeForGraaljs(fPort, obj) {
            //      return String.fromCharCode.apply(String, Encode(fPort, JSON.parse(obj)));
            // }
            engine.eval("function EncodeForGraaljs(fPort, obj) { return String.fromCharCode.apply(String, Encode(fPort, JSON.parse(obj))); }");
        }

        invocable = (Invocable) engine;
    }

    public <T> T invokeFunction(String functionName, Class<T> resultType, Object... args) throws ScriptException, NoSuchMethodException {
        Object result = invocable.invokeFunction(functionName, args);

        if (result == null) {
            return null;
        }

        if (resultType.isAssignableFrom(Map.class)) {
            return (T) deepClone((Map<String, Object>) result);
        } else if (resultType.isAssignableFrom(result.getClass())) {
            return resultType.cast(result);
        } else {
            throw new IllegalArgumentException("Invoked fuction returned an unprocessable response.");
        }
    }
}
