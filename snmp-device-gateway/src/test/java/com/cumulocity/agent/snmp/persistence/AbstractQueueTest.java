package com.cumulocity.agent.snmp.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractQueueTest {

    private Path persistentFolderPath;

    private AbstractQueueImplForTest abstractQueueImplForTest;

    @Before
    public void setUp() {
        persistentFolderPath = Paths.get(
                System.getProperty("user.home"),
                ".snmp",
                this.getClass().getSimpleName().toLowerCase(),
                "chronicle",
                "queues",
                AbstractQueueImplForTest.class.getSimpleName().toLowerCase());

        clearParentFolder();

        abstractQueueImplForTest = new AbstractQueueImplForTest();
    }

    @After
    public void tearDown() {
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

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEIfQueueNameNotProvided() {
        new AbstractQueue(null, persistentFolderPath.toFile()) {};
    }

    @Test
    public void shouldCreateMapWithCorrectName() {
        assertEquals(AbstractQueueImplForTest.class.getSimpleName(), abstractQueueImplForTest.getName());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEIfPersistenceFolderNotProvided() {
        new AbstractQueue("QUEUE_ONE", null) {};
    }

    @Test
    public void shouldCreateAndPersistInFile() {
        assertTrue(abstractQueueImplForTest.getPersistenceFolder().exists());
        assertEquals(persistentFolderPath.toString(), abstractQueueImplForTest.getPersistenceFolder().getPath());
    }

    @Test
    public void shouldEnqueueSuccessfully() {
        abstractQueueImplForTest.enqueue("TEST MESSAGE 1");

        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForEnqueueIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        abstractQueueImplForTest.enqueue("TEST MESSAGE 1");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfEnqueueWithNullMessage() {
        abstractQueueImplForTest.enqueue(null);
    }

    @Test
    public void shouldDequeueSuccessfully() {
        assertNull(abstractQueueImplForTest.dequeue());

        abstractQueueImplForTest.enqueue("TEST MESSAGE 1");
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.dequeue());

        abstractQueueImplForTest.enqueue("TEST MESSAGE 2");
        assertEquals("TEST MESSAGE 2", abstractQueueImplForTest.dequeue());

        assertNull(abstractQueueImplForTest.dequeue());
        assertNull(abstractQueueImplForTest.dequeue());

        abstractQueueImplForTest.enqueue("TEST MESSAGE 3");
        abstractQueueImplForTest.enqueue("TEST MESSAGE 4");
        abstractQueueImplForTest.enqueue("TEST MESSAGE 5");
        assertEquals("TEST MESSAGE 3", abstractQueueImplForTest.dequeue());
        assertEquals("TEST MESSAGE 4", abstractQueueImplForTest.dequeue());
        assertEquals("TEST MESSAGE 5", abstractQueueImplForTest.dequeue());
        assertNull(abstractQueueImplForTest.dequeue());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForDequeueIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        abstractQueueImplForTest.dequeue();
    }

    @Test
    public void shouldPeekSuccessfully() {
        assertNull(abstractQueueImplForTest.peek());

        abstractQueueImplForTest.enqueue("TEST MESSAGE 1");
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek());

        abstractQueueImplForTest.enqueue("TEST MESSAGE 2");
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek());

        abstractQueueImplForTest.enqueue("TEST MESSAGE 3");
        abstractQueueImplForTest.enqueue("TEST MESSAGE 4");
        abstractQueueImplForTest.enqueue("TEST MESSAGE 5");
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek());
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek());
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek());


        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.dequeue());
        assertEquals("TEST MESSAGE 2", abstractQueueImplForTest.dequeue());
        assertEquals("TEST MESSAGE 3", abstractQueueImplForTest.dequeue());
        assertEquals("TEST MESSAGE 4", abstractQueueImplForTest.dequeue());
        assertEquals("TEST MESSAGE 5", abstractQueueImplForTest.dequeue());
        assertNull(abstractQueueImplForTest.dequeue());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForPeekIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        abstractQueueImplForTest.peek();
    }

    @Test
    public void shouldDrainQueueWhenMaxElementsLessThanTheQueueSize() {
        int maxElements = 7;
        int queueSize = 10;

        enqueue(queueSize);

        // DRAIN maxElements TO
        List<String> collectionToDrainTo = new ArrayList<>();
        abstractQueueImplForTest.drainTo(collectionToDrainTo, maxElements);

        assertEquals(maxElements, collectionToDrainTo.size());
        assertMessages(0, maxElements, collectionToDrainTo);

        // DRAIN rest of the messages (queueSize - maxElements) TO
        collectionToDrainTo.clear();
        abstractQueueImplForTest.drainTo(collectionToDrainTo, maxElements);

        assertEquals((queueSize-maxElements), collectionToDrainTo.size());
        assertMessages(maxElements, (queueSize-maxElements), collectionToDrainTo);
    }

    @Test
    public void shouldDrainQueueWhenMaxElementsEqualToTheQueueSize() {
        int maxElements = 10;
        int queueSize = 10;

        enqueue(queueSize);

        // DRAIN TO
        List<String> collectionToDrainTo = new ArrayList<>();
        abstractQueueImplForTest.drainTo(collectionToDrainTo, maxElements);

        assertEquals(maxElements, collectionToDrainTo.size());
        assertMessages(0, maxElements, collectionToDrainTo);
    }

    @Test
    public void shouldDrainQueueWhenMaxElementsGreaterThanTheQueueSize() {
        int maxElements = 100;
        int queueSize = 10;

        enqueue(queueSize);

        // DRAIN TO
        List<String> collectionToDrainTo = new ArrayList<>();
        abstractQueueImplForTest.drainTo(collectionToDrainTo, maxElements);

        assertEquals(queueSize, collectionToDrainTo.size());
        assertMessages(0, queueSize, collectionToDrainTo);
    }

    private void assertMessages(int startIndex, int count, List<String> messages) {
        for(int i=0; i<count; i++) {
            assertEquals("TEST MESSAGE " + (startIndex + i), messages.get(i));
        }
    }

    private void enqueue(int count) {
        for(int i=0; i<count; i++) {
            abstractQueueImplForTest.enqueue("TEST MESSAGE " + i);
        }
    }

    private class AbstractQueueImplForTest extends AbstractQueue {
        AbstractQueueImplForTest() {
            super(AbstractQueueImplForTest.class.getSimpleName(), persistentFolderPath.toFile());
        }
    }
}