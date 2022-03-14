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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

@Component(GraalJavaScriptEngine.GRAAL_JAVA_SCRIPT_ENGINE_BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GraalJavaScriptEngine implements JavaScriptEngine {
    private final Logger logger = LoggerFactory.getLogger(GraalJavaScriptEngine.class);

    public static final String GRAAL_JAVA_SCRIPT_ENGINE_BEAN_NAME = "graalJavaScriptEngineTarget";

    private static final String JAVA_SCRIPT_RESOURCE_FOLDER_NAME = "/js/codec/*";

    private Invocable invocable;

    public GraalJavaScriptEngine() throws ScriptException, IOException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");

        // Load the JavaScript files present under the '/js/codec/' resources folder
        Resource[] javaScriptResources = new PathMatchingResourcePatternResolver().getResources(JAVA_SCRIPT_RESOURCE_FOLDER_NAME);
        for (Resource oneJavaScriptResource : javaScriptResources) {
            try (InputStreamReader scriptReader = new InputStreamReader(oneJavaScriptResource.getInputStream())) {
                engine.eval(scriptReader);
                logger.info("Loaded the JavaScript file {}", oneJavaScriptResource.getFilename());
            } catch (IOException | ScriptException e) {
                logger.error("Error while loading the JavaScript file '{}'", oneJavaScriptResource.getFilename(), e);
                throw e;
            }
        }

        invocable = (Invocable) engine;
    }

    public <T> T invokeFunction(String functionName, Class<T> resultType, Object... args) throws ScriptException, NoSuchMethodException {
        Object result;
        try {
            result = invocable.invokeFunction(functionName, args);
            if (result == null) {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error invoking the function {}", functionName, e);
            throw e;
        }

        if (resultType.isAssignableFrom(result.getClass())) {
            return resultType.cast(result);
        } else {
            throw new IllegalArgumentException("Return type of the invoked function doesn't match the passed in resultType.");
        }
    }
}
