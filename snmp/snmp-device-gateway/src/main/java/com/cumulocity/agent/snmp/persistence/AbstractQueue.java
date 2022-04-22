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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.StoreFileListener;
import net.openhft.chronicle.queue.impl.single.QueueFileShrinkManager;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.threads.Pauser;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.NoDocumentContext;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Queue implementation backed by the persistence Chronicle Queue
 */

@Slf4j
public abstract class AbstractQueue implements Queue {

    private static final int ENQUEUE_RETRY_LIMIT = 5;

    private final String name;
    private final File persistenceFolder;

    private final ChronicleQueue chronicleQueue;

    private final ExcerptTailer tailer;

    private final ReadWriteLock LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE = new ReentrantReadWriteLock(true);
    private final Pauser pauser = Pauser.balanced();

    private boolean isClosed = false;

    static {
        // Since the rolling cycle is set to minutely and we also delete the old files periodically,
        // we are fine with not shrinking the data files.
        // Hence we disable the data file shrinking
        if (System.getProperty("chronicle.queue.disableFileShrinking") == null) {
            System.setProperty("chronicle.queue.disableFileShrinking", Boolean.TRUE.toString());
        }
    }

    public AbstractQueue(String queueName, File persistenceFolder) {
        if (queueName == null) {
            throw new NullPointerException("queueName");
        }
        this.name = queueName;

        if (persistenceFolder == null) {
            throw new NullPointerException("persistenceFolder");
        }
        if(!persistenceFolder.exists()) {
            persistenceFolder.mkdirs();
        }
        this.persistenceFolder = persistenceFolder;

        log.info("Creating/Loading '{}' Queue, backed by the folder '{}'", this.name, persistenceFolder.getPath());

        // Create producer/consumer queue
        StoreFileListenerForDeletion storeFileListenerForDeletionOfReleasedFiles = new StoreFileListenerForDeletion(this.name);
        this.chronicleQueue = SingleChronicleQueueBuilder
                .single(persistenceFolder)
                .rollCycle(RollCycles.MINUTELY)
                .storeFileListener(storeFileListenerForDeletionOfReleasedFiles)
                .build();

        // Create a Tailer for the queue and later
        // Queue Appenders are also acquired over the same
        this.tailer = this.chronicleQueue.createTailer(this.name);

        storeFileListenerForDeletionOfReleasedFiles.setTailer(this.tailer);
    }

    public String getName() {
        return this.name;
    }

    public File getPersistenceFolder() {
        return this.persistenceFolder;
    }

    @Override
    public void enqueue(Message message) {
        if (message == null) {
            throw new NullPointerException("message");
        }

        if(isClosed) {
            throw new IllegalStateException("Cannot call enqueue as the '" + this.name + "' Queue is closed.");
        }

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();

        try {

            // Retry the enqueue operation, in case of errors, usually due to
            // the timeout errors thrown by Chronicle Queue implementation
            // while acquiring some lock internally.
            boolean retry;
            int retryCount = 0;
            do {
                try {
                    chronicleQueue.acquireAppender().writeDocument(message);
                    retry = false;
                } catch(Throwable t) {
                    if(++retryCount < ENQUEUE_RETRY_LIMIT) {
                        log.info("Enqueue to the '{}' Queue, resulted in a timeout. Retrying the enqueue operation, retry count {}.", this.name, retryCount);
                        log.debug("Stacktrace:", t);
                        retry = true;
                    }
                    else {
                        log.error("Enqueue to the '{}' Queue, resulted in a timeout.", this.name, t);
                        throw t;
                    }
                }

                pauser.unpause();

            } while(retry);
        } finally {
            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }
    }

    @Override
    public void backout(Message message) {
        if (message == null) {
            throw new NullPointerException("message");
        }

        message.incrementBackoutCount();

        // Since Chronicle Queue doesn't support inserting message at the beginning
        // of the queue, we just enqueue the message at the end of the queue.
        enqueue(message);
    }

    @Override
    public synchronized Message peek() {
        if(isClosed) {
            throw new IllegalStateException("Cannot peek as the '" + this.name + "' Queue is closed.");
        }

        Message returnValue = null;

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();

        DocumentContext documentContext = null;
        try {
            documentContext = tailer.readingDocument();
            if(documentContext.isPresent()) {
                pauser.reset();

                returnValue = new Message(documentContext.wire());
            }
            else {
                pauser.pause();
            }
        } catch (Throwable t) {
            log.error("Peek from the '{}' Queue, resulted in an unexpected error.", this.name, t);

            returnValue = null;
        } finally {
            if(documentContext != null) {
                documentContext.rollbackOnClose();
                documentContext.close();
            }

            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }

        return returnValue;
    }

    @Override
    public synchronized Message dequeue() {
        if(isClosed) {
            throw new IllegalStateException("Cannot call dequeue as the '" + this.name + "' Queue is closed.");
        }

        Message returnValue = null;

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();

        DocumentContext documentContext = null;
        try {
            documentContext = tailer.readingDocument();
            if(documentContext.isPresent()) {
                pauser.reset();

                returnValue = new Message(documentContext.wire());
            }
            else {
                pauser.pause();
            }
        } catch (Throwable t) {
            log.error("Dequeue from the '{}' Queue, resulted in an unexpected error. No message dequeued.", this.name, t);
            if(documentContext != null) {
                documentContext.rollbackOnClose();
            }

            returnValue = null;
        } finally {
            if(documentContext != null) {
                documentContext.close();
            }

            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }

        return returnValue;
    }

    @Override
    public synchronized int drainTo(Collection<Message> collection, int maxElements) {
        if(isClosed) {
            throw new IllegalStateException("Cannot call drainTo as the '" + this.name + "' Queue is closed.");
        }

        if (collection == null) {
            throw new NullPointerException("collection");
        }
        if (maxElements <= 0) {
            return 0;
        }

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();

        int elementCount = 0;
        DocumentContext documentContext = null;
        try {
            documentContext = tailer.readingDocument();
            if(documentContext.isPresent()) {
                pauser.reset();

                while(documentContext.isPresent()) {
                    collection.add(new Message(documentContext.wire()));
                    ++elementCount;

                    documentContext.close();
                    documentContext = NoDocumentContext.INSTANCE;

                    if(elementCount < maxElements) {
                        documentContext = tailer.readingDocument();
                    }
                }
            }
            else {
                pauser.pause();
            }
        } catch (Throwable t) {
            log.error("Draining of the '{}' Queue, resulted in an unexpected error. Returning the messages already drained.", this.name, t);
            if(documentContext != null) {
                documentContext.rollbackOnClose();
            }
        } finally {
            if(documentContext != null) {
                documentContext.close();
            }

            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }

        return elementCount;
    }

    @Override
    public void close() {
        if(!isClosed) {
            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.writeLock().lock();
            try {
                isClosed = true;

                chronicleQueue.close();
            } finally {
                LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.writeLock().unlock();
            }

            log.info("'{}' Queue closed.", this.name);
        }
        else {
            log.info("'{}' Queue already closed.", this.name);
        }
    }



    /**
     * Store files created by the Chronicle Queue are not deleted automatically.
     * This is a callback class invoked by teh Chronicle Queue when a new file is acquired and released.
     * We remove the files when released by the tailer/consumer if it is not locked by any other process.
     */
    @Slf4j
    static class StoreFileListenerForDeletion implements StoreFileListener {

        private final String queueName;

        @Setter
        private ExcerptTailer tailer;

        StoreFileListenerForDeletion(String queueName) {
            this.queueName = queueName;
        }

        @Override
        public void onAcquired(int cycle, File acquiredFile) {
            log.trace("'{}' Queue, acquired the store file '{}' for cycle '{}'", queueName, acquiredFile.getPath(), cycle);
        }

        @Override
        public void onReleased(int cycle, final File releasedFile) {
            log.trace("'{}' Queue, released the store file '{}' for cycle '{}'", queueName, releasedFile.getPath(), cycle);

            if(cycle >= this.tailer.cycle()) {
                // This means, the Tailer hasn't finished processing the file which is released.
                // Note: This occurs when the Appender releases the current file to rollover,
                // to write into a new file. Skip the file cleanup logic in this case.

                log.trace("File cleanup of the '{}' Queue skipped, as the Tailer is still at the cycle {}.", queueName, this.tailer.cycle());
                return;
            }

            final String releasedFileName = releasedFile.getName().toLowerCase();

            File[] filesInTheFolder = releasedFile.getParentFile().listFiles((dir, name) -> {
                File file = new File(dir, name);
                String fileName = name.toLowerCase();

                return file.isFile()
                        && !fileName.startsWith("metadata") // Skip the metadata file
                        && fileName.endsWith(".cq4")        // Skip files with extensions other than .cq4 (select only data files)
                        && (fileName.compareTo(releasedFileName) < 0); // Select only files which are older than the file being released
            });

            if(filesInTheFolder != null) {
                for (File fileToDelete : filesInTheFolder) {
                    if(fileToDelete.delete()) {
                        log.trace("Deleted the old store file '{}' of the '{}' Queue", fileToDelete.toString(), queueName);
                    }
//                else {
//                    // This case can be ignored, as the handle to the file being deleted may still be held by a Tailer.
//                    // This file will eventually be deleted in subsequent cleanup cycles.
//                }
                }
            }
        }
    }
}
