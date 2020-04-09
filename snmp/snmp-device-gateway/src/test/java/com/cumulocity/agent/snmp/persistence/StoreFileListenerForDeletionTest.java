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

package com.cumulocity.agent.snmp.persistence;

import org.junit.After;
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


    @Test
    public void shouldDeleteOlderFiles() {
        try {
            clearParentFolder();
            parentFolderPath.toFile().mkdirs();

            Files.createFile(metadataFile.toPath());

            Thread.sleep(100);

            Files.createFile(oldFile_1.toPath());
            Files.createFile(oldFile_2.toPath());
            Files.createFile(oldFile_3.toPath());

            Thread.sleep(100);

            Files.createFile(releasedFile.toPath());

            Thread.sleep(100);

            Files.createFile(newFile_1.toPath());
            Files.createFile(newFile_2.toPath());
            Files.createFile(newFile_3.toPath());
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // Raise an onReleased event
        new AbstractQueue.StoreFileListenerForDeletion(this.getClass().getSimpleName()).onReleased(0, releasedFile);

        assertTrue(oldFile_1.exists());  // Not deleted, as the file does not have a .cq4 extension
        assertFalse(oldFile_2.exists()); // Deleted, as the file is created before the released file
        assertFalse(oldFile_3.exists()); // Deleted, as the file is created before the released file


        assertTrue(newFile_1.exists()); // Not deleted, as the file does not have a .cq4 extension and is accessed/updated after the released file
        assertTrue(newFile_2.exists()); // Not deleted, as the file is created after the released file
        assertTrue(newFile_3.exists()); // Not deleted, as the file is created after the released file

        assertTrue(releasedFile.exists()); // Not deleted, as this is the released file having the same creation time

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
