/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
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

package com.cumulocity.agent.snmp.persistence;

import net.openhft.chronicle.queue.impl.single.QueueFileShrinkManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cumulocity.agent.snmp.util.WorkspaceUtils.getWorkspacePath;
import static org.junit.Assert.*;

@Ignore("MTM-55935")
public class AbstractQueueTest {

    private Path persistentFolderPath;

    private AbstractQueueImplForTest abstractQueueImplForTest;

    @Before
    public void setUp() {
        persistentFolderPath = Paths.get(
                getWorkspacePath(),
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
                persistentFolderPath.getParent().toFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEIfQueueNameNotProvided() {
        try(AbstractQueue queue = new AbstractQueue(null, persistentFolderPath.toFile()) {}){
        }
    }

    @Test
    public void shouldCreateMapWithCorrectName() {
        assertEquals(AbstractQueueImplForTest.class.getSimpleName(), abstractQueueImplForTest.getName());
        assertTrue(QueueFileShrinkManager.DISABLE_QUEUE_FILE_SHRINKING);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEIfPersistenceFolderNotProvided() {
    	try(AbstractQueue queue = new AbstractQueue("QUEUE_ONE", null) {}) {
    	}
    }

    @Test
    public void shouldCreateAndPersistInFile() {
        assertTrue(abstractQueueImplForTest.getPersistenceFolder().exists());
        assertEquals(persistentFolderPath.toString(), abstractQueueImplForTest.getPersistenceFolder().getPath());
    }

    @Test
    public void shouldEnqueueSuccessfully() {
        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 1"));

        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek().getPayload());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForEnqueueIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 1"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfEnqueueWithNullMessage() {
        abstractQueueImplForTest.enqueue(null);
    }

    @Test
    public void shouldDequeueSuccessfully() {
        assertNull(abstractQueueImplForTest.dequeue());

        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 1"));
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.dequeue().getPayload());

        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 2"));
        assertEquals("TEST MESSAGE 2", abstractQueueImplForTest.dequeue().getPayload());

        assertNull(abstractQueueImplForTest.dequeue());
        assertNull(abstractQueueImplForTest.dequeue());

        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 3"));
        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 4"));
        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 5"));
        assertEquals("TEST MESSAGE 3", abstractQueueImplForTest.dequeue().getPayload());
        assertEquals("TEST MESSAGE 4", abstractQueueImplForTest.dequeue().getPayload());
        assertEquals("TEST MESSAGE 5", abstractQueueImplForTest.dequeue().getPayload());
        assertNull(abstractQueueImplForTest.dequeue());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForDequeueIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        abstractQueueImplForTest.dequeue();
    }

    @Test
    public void shouldBackoutSuccessfully() {
        Message originalMessage = new Message("TEST MESSAGE 1", (short) 0);
        abstractQueueImplForTest.backout(originalMessage);

        Message dequeuedMessage = abstractQueueImplForTest.dequeue();
        assertEquals(originalMessage.getPayload(), dequeuedMessage.getPayload());
        assertEquals(1, dequeuedMessage.getBackoutCount());
    }

    @Test(expected = NullPointerException.class)
    public void should_FailIfMessageIsNull() {
        abstractQueueImplForTest.backout(null);
    }

    @Ignore
    @Test
    public void shouldPeekSuccessfully() {
        assertNull(abstractQueueImplForTest.peek());

        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 1"));
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek().getPayload());

        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 2"));
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek().getPayload());

        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 3"));
        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 4"));
        abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE 5"));
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek().getPayload());
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek().getPayload());
        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.peek().getPayload());


        assertEquals("TEST MESSAGE 1", abstractQueueImplForTest.dequeue().getPayload());
        assertEquals("TEST MESSAGE 2", abstractQueueImplForTest.dequeue().getPayload());
        assertEquals("TEST MESSAGE 3", abstractQueueImplForTest.dequeue().getPayload());
        assertEquals("TEST MESSAGE 4", abstractQueueImplForTest.dequeue().getPayload());
        assertEquals("TEST MESSAGE 5", abstractQueueImplForTest.dequeue().getPayload());
        assertNull(abstractQueueImplForTest.dequeue());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForPeekIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        abstractQueueImplForTest.peek();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForDrainToIfQueueIsClosed() {
        abstractQueueImplForTest.close();
        // DRAIN TO
        List<Message> collectionToDrainTo = Collections.emptyList();
        abstractQueueImplForTest.drainTo(collectionToDrainTo, 10);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionForDrainToWhenPassedCollectionIsNull() {
        // DRAIN TO
        abstractQueueImplForTest.drainTo(null, 10);
    }

    @Test
    public void shouldReturnZeroWhenTheMaxElementsPassedIsEqualOrLessThanZero() {
        List<Message> collectionToDrainTo = Collections.emptyList();

        // DRAIN TO 0
        assertEquals(0, abstractQueueImplForTest.drainTo(collectionToDrainTo, 0));
        assertEquals(Collections.EMPTY_LIST, collectionToDrainTo);

        // DRAIN TO -1
        assertEquals(0, abstractQueueImplForTest.drainTo(collectionToDrainTo, -1));
        assertEquals(Collections.EMPTY_LIST, collectionToDrainTo);
    }

    @Test
    public void shouldDrainQueueWhenMaxElementsLessThanTheQueueSize() {
        int maxElements = 7;
        int queueSize = 10;

        enqueue(queueSize);

        // DRAIN maxElements TO
        List<Message> collectionToDrainTo = new ArrayList<>();
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
        List<Message> collectionToDrainTo = new ArrayList<>();
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
        List<Message> collectionToDrainTo = new ArrayList<>();
        abstractQueueImplForTest.drainTo(collectionToDrainTo, maxElements);

        assertEquals(queueSize, collectionToDrainTo.size());
        assertMessages(0, queueSize, collectionToDrainTo);
    }



    private void assertMessages(int startIndex, int count, List<Message> messages) {
        for(int i=0; i<count; i++) {
            assertEquals("TEST MESSAGE " + (startIndex + i), messages.get(i).getPayload());
        }
    }

    private void enqueue(int count) {
        for(int i=0; i<count; i++) {
            abstractQueueImplForTest.enqueue(new Message("TEST MESSAGE " + i));
        }
    }

    private class AbstractQueueImplForTest extends AbstractQueue {
        AbstractQueueImplForTest() {
            super(AbstractQueueImplForTest.class.getSimpleName(), persistentFolderPath.toFile());
        }
    }

    public class StoreFileListenerForDeletionTest {

        private Path parentFolderPath = Paths.get(
                getWorkspacePath(),
                ".snmp",
                this.getClass().getSimpleName().toLowerCase(),
                "chronicle",
                "queues",
                this.getClass().getSimpleName().toLowerCase());

        private File oldFile_1 = new File(parentFolderPath.toFile(), "oldFile_1.txt");
        private File oldFile_2 = new File(parentFolderPath.toFile(), "oldFile_2.cq4");
        private File oldFile_3 = new File(parentFolderPath.toFile(), "oldFile_3.cq4");

        private File newFile_1 = new File(parentFolderPath.toFile(), "newFile_1.txt");
        private File newFile_2 = new File(parentFolderPath.toFile(), "newFile_2.cq4");
        private File newFile_3 = new File(parentFolderPath.toFile(), "newFile_3.cq4");

        private File releasedFile = new File(parentFolderPath.toFile(), "releasedFile.cq4");

        private File metadataFile = new File(parentFolderPath.toFile(), "metadata.cq4t");


        @Before
        public void setUp() {
            try {
                clearParentFolder();
                parentFolderPath.toFile().mkdirs();

                Files.createFile(oldFile_1.toPath());
                Files.createFile(oldFile_2.toPath());
                Files.createFile(oldFile_3.toPath());

                Files.createFile(newFile_1.toPath());
                Files.createFile(newFile_2.toPath());
                Files.createFile(newFile_3.toPath());

                Files.createFile(releasedFile.toPath());

                Files.createFile(metadataFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        @Test
        public void shouldDeleteOlderFiles() {
            try {
                Files.write(oldFile_1.toPath(), "SOME TEXT".getBytes());
                Files.write(oldFile_2.toPath(), "SOME TEXT".getBytes());
                Files.write(oldFile_3.toPath(), "SOME TEXT".getBytes());

                Thread.sleep(100);

                Files.write(releasedFile.toPath(), "SOME TEXT".getBytes());

                Files.write(metadataFile.toPath(), "SOME TEXT".getBytes());

                Thread.sleep(100);

                Files.write(newFile_1.toPath(), "SOME TEXT".getBytes());
                Files.write(newFile_2.toPath(), "SOME TEXT".getBytes());
                Files.write(newFile_3.toPath(), "SOME TEXT".getBytes());
            } catch (Throwable e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

            // Raise an onReleased event
            new AbstractQueue.StoreFileListenerForDeletion(this.getClass().getSimpleName()).onReleased(0, releasedFile);

            assertTrue(oldFile_1.exists());  // Not deleted, as the file does not have a .cq4 extension
            assertFalse(oldFile_2.exists()); // Deleted, as the file is accessed/updated before the released file
            assertFalse(oldFile_3.exists()); // Deleted, as the file is accessed/updated before the released file


            assertTrue(newFile_1.exists()); // Not deleted, as the file does not have a .cq4 extension and is accessed/updated after the released file
            assertTrue(newFile_2.exists()); // Not deleted, as the file is accessed/updated after the released file
            assertTrue(newFile_3.exists()); // Not deleted, as the file is accessed/updated after the released file

            assertTrue(releasedFile.exists()); // Not deleted, as this is the released file having the same last accessed/updated time

            assertTrue(metadataFile.exists()); // Not deleted, as this is a metadata file
        }

        @After
        public void tearDown() {
            try {
                clearParentFolder();
            } catch (IOException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        private void clearParentFolder() throws IOException {
            if(parentFolderPath != null && parentFolderPath.toFile().exists()) {
                Files.list(parentFolderPath).forEach(fileInTheFolder -> fileInTheFolder.toFile().delete());
                parentFolderPath.toFile().delete();
            }
        }
    }
}