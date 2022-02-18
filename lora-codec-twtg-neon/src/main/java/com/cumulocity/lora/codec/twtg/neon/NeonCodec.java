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

package com.cumulocity.lora.codec.twtg.neon;

import com.cumulocity.microservice.lpwan.codec.Codec;
import com.cumulocity.microservice.lpwan.codec.model.DeviceInfo;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class NeonCodec implements Codec {
    @Override
    public @NotNull @NotEmpty Set<DeviceInfo> supportsDevices() {
        DeviceInfo neonTemperatureSensor = new DeviceInfo("TWTG", "Neon Temperature Sensor");

        return Stream.of(neonTemperatureSensor).collect(Collectors.toCollection(HashSet::new));
    }
}
