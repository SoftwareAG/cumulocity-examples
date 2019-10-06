package com.cumulocity.agent.snmp.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class StoreFileListenerForDeletionTest {

    private Path parentFolderPath = Paths.get(
            System.getProperty("user.home"),
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
