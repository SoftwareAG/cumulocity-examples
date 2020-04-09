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

package com.cumulocity.agent.snmp.platform.pubsub.queue;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AlarmQueueTest {

    @Mock
    private GatewayProperties gatewayProperties;

    private AlarmQueue alarmQueue;

    private Path persistentFolderPath;


    @Before
    public void setUp() {
        Mockito.when(gatewayProperties.getGatewayIdentifier()).thenReturn(this.getClass().getSimpleName());

        persistentFolderPath = Paths.get(
                System.getProperty("user.home"),
                ".snmp",
                gatewayProperties.getGatewayIdentifier().toLowerCase(),
                "chronicle",
                "queues",
                "ALARM".toLowerCase());

        clearParentFolder();

        alarmQueue = new AlarmQueue(gatewayProperties);
    }

    @After
    public void tearDown() {
        if(alarmQueue != null) {
            alarmQueue.close();
        }

        clearParentFolder();
    }

    private void clearParentFolder() {
        if(persistentFolderPath.toFile().exists()) {
            try {
                Files.list(persistentFolderPath).forEach(fileInTheFolder -> fileInTheFolder.toFile().delete());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldCreateQueueWithCorrectName() {
        assertEquals("ALARM", alarmQueue.getName());
    }

    @Test
    public void shouldCreateRequiredPersistenceFolder() {
        assertTrue(alarmQueue.getPersistenceFolder().exists());
        assertEquals(persistentFolderPath.toString(), alarmQueue.getPersistenceFolder().getPath());
    }
}