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

        alarmQueue = Mockito.spy(new AlarmQueue(gatewayProperties));
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