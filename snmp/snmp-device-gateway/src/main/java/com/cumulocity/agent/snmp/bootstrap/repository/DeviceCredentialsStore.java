/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
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

package com.cumulocity.agent.snmp.bootstrap.repository;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;

@Repository
public class DeviceCredentialsStore extends AbstractMap<DeviceCredentialsKey, String> {

    private static final String DEVICE_CREDENTIALS_STORE = "device-credentials-store";

    @Autowired
    DeviceCredentialsStore(GatewayProperties gatewayProperties) {
        super(DEVICE_CREDENTIALS_STORE,
                DeviceCredentialsKey.class,
                100,
                String.class,
                10_000,
                10, // Chronicle Map is optimized for 10 entries as we actually store only one entry with device credentials
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        gatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "maps",
                        DEVICE_CREDENTIALS_STORE.toLowerCase() + ".dat").toFile()
        );
    }
}
