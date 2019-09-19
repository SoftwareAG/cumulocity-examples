package com.cumulocity.agent.snmp.persistence;

import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.StoreFileListener;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.threads.Pauser;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.NoDocumentContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Queue implementation backed by the persistence Chronicle Queue
 */

@Slf4j
public abstract class AbstractQueue implements Queue {

    private String name;

    private ChronicleQueue producerQueue;
    private ChronicleQueue consumerQueue;
    private ExcerptTailer tailer;

    private ReadWriteLock LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE = new ReentrantReadWriteLock(true);
    private boolean isClosed = false;

    private Pauser pauser = Pauser.balanced();


    public AbstractQueue(String queueName, File persistenceFolder) {
        if (queueName == null) {
            throw new NullPointerException("queueName");
        }
        this.name = queueName;

        if(!persistenceFolder.exists()) {
            persistenceFolder.mkdirs();
        }

        log.info("Creating/Loading '{}' Queue, backed by the folder '{}'", this.name, persistenceFolder.getPath());

        // Create producer queue
        this.producerQueue = SingleChronicleQueueBuilder
                .single(persistenceFolder)
                .rollCycle(RollCycles.MINUTELY)
                .build();


        // Create reader queue
        this.consumerQueue = SingleChronicleQueueBuilder
                .single(persistenceFolder)
                .rollCycle(RollCycles.MINUTELY)
                .storeFileListener(new StoreFileListenerForDeletion(this.name))
                .build();

        // Create tailer
        this.tailer = this.consumerQueue.createTailer(this.name);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void enqueue(String message) {
        if(isClosed) {
            throw new IllegalStateException("Cannot call enqueue after the '" + this.name + "' Queue is closed.");
        }
        if (message == null) {
            throw new NullPointerException("message");
        }

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();
        try {
            producerQueue.acquireAppender().writeText(message);
            pauser.unpause();
        } catch(Throwable t) {
            log.error("Enqueue to the '" + this.name + "' Queue, resulted in a timeout.", t);
            throw t;
        } finally {
            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }
    }

    @Override
    public synchronized String peek() {
        if(isClosed) {
            throw new IllegalStateException("Cannot call peek after the '" + this.name + "' Queue is closed.");
        }

        String returnValue = null;

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();
        DocumentContext documentContext = tailer.readingDocument();
        try {
            if(documentContext.isPresent()) {
                pauser.reset();

                returnValue = documentContext.wire().read().text();
            }
            else {
                pauser.pause();
            }
        } catch (Throwable t) {
            log.error("Peek from the '" + this.name + "' Queue, resulted in an unexpected error.", t);

            returnValue = null;
        } finally {
            documentContext.rollbackOnClose();
            documentContext.close();

            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }

        return returnValue;
    }

    @Override
    public synchronized String dequeue() {
        if(isClosed) {
            throw new IllegalStateException("Cannot call dequeue after the '" + this.name + "' Queue is closed.");
        }

        String returnValue = null;

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();
        DocumentContext documentContext = tailer.readingDocument();
        try {
            if(documentContext.isPresent()) {
                pauser.reset();

                returnValue = documentContext.wire().read().text();
            }
            else {
                pauser.pause();
            }
        } catch (Throwable t) {
            log.error("Dequeue from the '" + this.name + "' Queue, resulted in an unexpected error. No message dequeued.", t);
            documentContext.rollbackOnClose();

            returnValue = null;
        } finally {
            documentContext.close();

            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }

        return returnValue;
    }

    @Override
    public synchronized int drainTo(Collection<String> collection, int maxElements) {
        if(isClosed) {
            throw new IllegalStateException("Cannot call drainTo after the '" + this.name + "' Queue is closed.");
        }

        if (collection == null) {
            throw new NullPointerException("collection");
        }
        if (maxElements <= 0) {
            return 0;
        }

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();
        DocumentContext documentContext = tailer.readingDocument();
        int elementCount = 0;
        try {
            if(documentContext.isPresent()) {
                pauser.reset();

                while(documentContext.isPresent()) {
                    collection.add(documentContext.wire().read().text());
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
            log.error("Draining of the '" + this.name + "' Queue, resulted in an unexpected error. Returning the messages already drained.", t);
            documentContext.rollbackOnClose();
        } finally {
            documentContext.close();

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

                producerQueue.close();
                consumerQueue.close();
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
     * We delete the files when released by the tailer/consumer if it is not locked by any other process.
     */
    @Slf4j
    private static class StoreFileListenerForDeletion implements StoreFileListener {

        private final String queueName;

        private  StoreFileListenerForDeletion(String queueName) {
            this.queueName = queueName;
        }

        @Override
        public void onAcquired(int cycle, File acquiredFile) {
            log.trace("'{}' Queue, acquired the store file '{}' for cycle '{}'", queueName, acquiredFile.getPath(), cycle);
        }

        @Override
        public void onReleased(int cycle, final File releasedFile) {
            log.trace("'{}' Queue, released the store file '{}' for cycle '{}'", queueName, releasedFile.getPath(), cycle);

            try {
                final FileTime releasedFileCreationTime = Files.readAttributes(releasedFile.toPath(), BasicFileAttributes.class).creationTime();

                Files.list(releasedFile.getParentFile().toPath())
                        .filter(fileInTheFolder -> !fileInTheFolder.getFileName().toString().toLowerCase().startsWith("metadata")) // Filter out the metadata files
                        .filter(fileInTheFolder -> {
                                    try {
                                        return (Files.readAttributes(fileInTheFolder, BasicFileAttributes.class).creationTime().compareTo(releasedFileCreationTime) < 0);
                                    } catch (IOException ioe) {
                                        log.error("Unexpected error while cleaning up old store files of the '" + queueName + "' Queue", ioe);
                                        return false;
                                    }
                                }
                        )
                        .forEach(fileInTheFolder -> {
                            try {
                                Files.delete(fileInTheFolder);
                                log.trace("Deleted the old store file '{}' of the '{}' Queue", fileInTheFolder, queueName);
                            } catch (IOException ioe) {
                                // Here the exception can be ignored, as the handle to the file being deleted
                                // may still be held by some other process. This will eventually be deleted
                                // in subsequent cleanup cycles.
                                log.trace("Could not delete the old store file '" + fileInTheFolder + "' of the '" + queueName + "' Queue", ioe);
                            }
                        });
            } catch (IOException ioe) {
                log.error("Unexpected error while cleaning up old store files of the '" + queueName + "' Queue", ioe);
            }
        }
    }
}
