package com.cumulocity.agent.snmp.platform.pubsub.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.cumulocity.agent.snmp.config.GatewayProperties;

@RunWith(MockitoJUnitRunner.class)
public class EventQueueTest {

    @Mock
    private GatewayProperties gatewayProperties;

    private EventQueue eventQueue;

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
                "EVENT".toLowerCase());

        clearParentFolder();

        eventQueue = Mockito.spy(new EventQueue(gatewayProperties));
    }

    @After
    public void tearDown() {
        if(eventQueue != null) {
            eventQueue.close();
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
        assertEquals("EVENT", eventQueue.getName());
    }

    @Test
    public void shouldCreateRequiredPersistenceFolder() {
        assertTrue(eventQueue.getPersistenceFolder().exists());
        assertEquals(persistentFolderPath.toString(), eventQueue.getPersistenceFolder().getPath());
    }
}