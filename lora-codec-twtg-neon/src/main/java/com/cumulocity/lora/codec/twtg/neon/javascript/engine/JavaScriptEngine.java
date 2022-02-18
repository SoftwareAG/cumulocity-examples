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

package com.cumulocity.lora.codec.twtg.neon.javascript.engine;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public interface JavaScriptEngine {

    <T> T invokeFunction(String functionName, Class<T> resultType, Object... args) throws ScriptException, NoSuchMethodException;

    default Map<String, Object> deepClone(Map<String, Object> original) {
        return original.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {

                    Object value = entry.getValue();
                    if (value == null) {
                        return null;
                    }

                    if (value.getClass().isArray()) {
                        return Arrays.stream(((Object[]) value)).map((v) -> {
                            if (v == null) {
                                return null;
                            }

                            if (Map.class.isAssignableFrom(v.getClass())) {
                                return deepClone((Map<String, Object>) v);
                            } else {
                                return v;
                            }
                        }).toArray(Object[]::new);
                    } else if (Map.class.isAssignableFrom(value.getClass())) {
                        return deepClone((Map<String, Object>) value);
                    } else {
                        return value;
                    }
                }));
    }
}
