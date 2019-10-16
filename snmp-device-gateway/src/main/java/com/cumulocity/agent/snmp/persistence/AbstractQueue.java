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

    private final String name;
    private final File persistenceFolder;

    private final ChronicleQueue producerQueue;
    private final ChronicleQueue consumerQueue;
    private final ExcerptTailer tailer;

    private final ReadWriteLock LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE = new ReentrantReadWriteLock(true);
    private final Pauser pauser = Pauser.balanced();

    private boolean isClosed = false;



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

    public File getPersistenceFolder() {
        return this.persistenceFolder;
    }

    @Override
    public void enqueue(String message) {
        if (message == null) {
            throw new NullPointerException("message");
        }

        if(isClosed) {
            throw new IllegalStateException("Cannot call enqueue as the '" + this.name + "' Queue is closed.");
        }

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();
        try {
            producerQueue.acquireAppender().writeText(message);
            pauser.unpause();
        } catch(Throwable t) {
            log.error("Enqueue to the '{}' Queue, resulted in a timeout.", this.name, t);
            throw t;
        } finally {
            LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().unlock();
        }
    }

    @Override
    public synchronized String peek() {
        if(isClosed) {
            throw new IllegalStateException("Cannot peek as the '" + this.name + "' Queue is closed.");
        }

        String returnValue = null;

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();

        DocumentContext documentContext = null;
        try {
            documentContext = tailer.readingDocument();
            if(documentContext.isPresent()) {
                pauser.reset();

                returnValue = documentContext.wire().read().text();
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
    public synchronized String dequeue() {
        if(isClosed) {
            throw new IllegalStateException("Cannot call dequeue as the '" + this.name + "' Queue is closed.");
        }

        String returnValue = null;

        LOCK_TO_ENSURE_NO_ONE_IS_ACCESSING_THE_QUEUE.readLock().lock();

        DocumentContext documentContext = null;
        try {
            documentContext = tailer.readingDocument();
            if(documentContext.isPresent()) {
                pauser.reset();

                returnValue = documentContext.wire().read().text();
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
    public synchronized int drainTo(Collection<String> collection, int maxElements) {
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
     * We remove the files when released by the tailer/consumer if it is not locked by any other process.
     */
    @Slf4j
    static class StoreFileListenerForDeletion implements StoreFileListener {

        private final String queueName;

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

            try {
                final FileTime releasedFileLastAccessTime = Files.readAttributes(releasedFile.toPath(), BasicFileAttributes.class).lastAccessTime();

                Files.list(releasedFile.getParentFile().toPath())
                        // Filter out folders and the metadata files. Basically select only files with '.cq4' extension
                        .filter(fileInTheFolder -> {
                            String fileName = fileInTheFolder.getFileName().toString().toLowerCase();

                            return fileInTheFolder.toFile().isFile() && !fileName.startsWith("metadata") && fileName.endsWith(".cq4");
                        })

                        // Select the files which were last accessed/modified earlier than the creation time of the file which is being released now
                        .filter(fileInTheFolder -> {
                                    try {
                                        return (Files.readAttributes(fileInTheFolder, BasicFileAttributes.class).lastAccessTime().compareTo(releasedFileLastAccessTime) < 0);
                                    } catch (IOException ioe) {
                                        log.error("Unexpected error while cleaning up old store files of the '{}' Queue", queueName, ioe);
                                        return false;
                                    }
                                }
                        )

                        // Delete the filtered files
                        .forEach(fileInTheFolder -> {
                            try {
                                Files.delete(fileInTheFolder);
                                log.trace("Deleted the old store file '{}' of the '{}' Queue", fileInTheFolder.toString(), queueName);
                            } catch (IOException ioe) {
                                // Here the exception can be ignored, as the handle to the file being deleted may still be held a Tailer.
                                // This file will eventually be deleted in subsequent cleanup cycles.
                                log.trace("Could not remove the old store file '{}' of the '{}' Queue", fileInTheFolder, queueName, ioe);
                            }
                        });

            } catch (IOException ioe) {
                log.error("Unexpected error while cleaning up old store files of the '{}' Queue", queueName, ioe);
            }
        }
    }
}
