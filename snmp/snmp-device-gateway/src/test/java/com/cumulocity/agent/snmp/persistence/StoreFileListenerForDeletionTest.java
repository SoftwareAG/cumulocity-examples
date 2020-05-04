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

import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StoreFileListenerForDeletionTest {

    @Mock
    private ExcerptTailer mockTailer;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(RollCycles.MINUTELY.format());
    private final Calendar NOW = new GregorianCalendar();

    private Path parentFolderPath = Paths.get(
            System.getProperty("user.home"),
            ".snmp",
            this.getClass().getSimpleName().toLowerCase());

    private final File parentFolder = parentFolderPath.toFile();

    private File fileWithWrongExtension = new File(parentFolder, "fileWithWrongExtension.txt");

    // File with the current date - 2 Minutes
    private File oldFile_1 = fileWithFormatedName(parentFolder, NOW, -2);

    // File with the current date - 1 Minute
    private File oldFile_2 = fileWithFormatedName(parentFolder, NOW, -1);

    // File with the current date
    private File releasedFile = fileWithFormatedName(parentFolder, NOW, 0);

    private File newFileWithWrongExtension = new File(parentFolder, "newFileWithWrongExtension.txt");

    // File with the current date + 1 Minute
    private File newFile_1 = fileWithFormatedName(parentFolder, NOW, +1);

    // File with the current date + 2 Minutes
    private File newFile_2 = fileWithFormatedName(parentFolder, NOW, +2);


    private File metadataFile = new File(parentFolder, "metadata.cq4t");


    private static File fileWithFormatedName(File parentFolder, Calendar now, int delta) {
        return new File(parentFolder,
                DATE_FORMAT.format(new GregorianCalendar(
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH),
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE) + delta).getTime()) + ".cq4");
    }

    @Before
    public void setUp() {
        try {
            clearParentFolder();

            parentFolder.mkdirs();

            Files.createFile(metadataFile.toPath());

            Files.createFile(fileWithWrongExtension.toPath());
            Files.createFile(oldFile_1.toPath());
            Files.createFile(oldFile_2.toPath());

            Files.createFile(releasedFile.toPath());

            Files.createFile(newFileWithWrongExtension.toPath());
            Files.createFile(newFile_1.toPath());
            Files.createFile(newFile_2.toPath());
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
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
        if(parentFolderPath != null && parentFolder.exists()) {
            Files.walk(parentFolderPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void should_Not_DeleteOlderFiles_As_TailersCycle_IsSameAs_ReleasedFileCycle() {
        // Raise an onReleased event
        AbstractQueue.StoreFileListenerForDeletion storeFileListenerForDeletion = new AbstractQueue.StoreFileListenerForDeletion(this.getClass().getSimpleName());
        storeFileListenerForDeletion.setTailer(mockTailer);

        int tailerCycle = 0;
        int releasedFileCycle = 0;
        Mockito.when(mockTailer.cycle()).thenReturn(tailerCycle);
        storeFileListenerForDeletion.onReleased(releasedFileCycle, releasedFile);

        // None of the files should be deleted as the Tailer's cycle < Released file's cycle
        assertTrue(fileWithWrongExtension.exists());  // Not deleted, as the file does not have a .cq4 extension
        assertTrue(oldFile_1.exists()); // Not Deleted
        assertTrue(oldFile_2.exists()); // Not Deleted


        assertTrue(newFileWithWrongExtension.exists()); // Not deleted, as the file does not have a .cq4 extension and is accessed/updated after the released file
        assertTrue(newFile_1.exists()); // Not deleted
        assertTrue(newFile_2.exists()); // Not deleted

        assertTrue(releasedFile.exists()); // Not deleted

        assertTrue(metadataFile.exists()); // Not deleted
    }

    @Test
    public void should_DeleteOlderFiles_As_TailersCycle_IsGreaterThan_ReleasedFileCycle() {
        // Raise an onReleased event
        AbstractQueue.StoreFileListenerForDeletion storeFileListenerForDeletion = new AbstractQueue.StoreFileListenerForDeletion(this.getClass().getSimpleName());
        storeFileListenerForDeletion.setTailer(mockTailer);

        int tailerCycle = 1;
        int releasedFileCycle = 0;
        Mockito.when(mockTailer.cycle()).thenReturn(tailerCycle);
        storeFileListenerForDeletion.onReleased(releasedFileCycle, releasedFile);

        assertTrue(fileWithWrongExtension.exists());  // Not deleted, as the file does not have a .cq4 extension
        assertFalse(oldFile_1.exists()); // Deleted, as the file is created before the released file
        assertFalse(oldFile_2.exists()); // Deleted, as the file is created before the released file


        assertTrue(newFileWithWrongExtension.exists()); // Not deleted, as the file does not have a .cq4 extension and is accessed/updated after the released file
        assertTrue(newFile_1.exists()); // Not deleted, as the file is created after the released file
        assertTrue(newFile_2.exists()); // Not deleted, as the file is created after the released file

        assertTrue(releasedFile.exists()); // Not deleted, as this is the released file having the same creation time

        assertTrue(metadataFile.exists()); // Not deleted, as this is a metadata file
    }

    @Test
    public void should_Not_DeleteOlderFiles_As_TailersCycle_IsLessThan_ReleasedFileCycle() {
        // Raise an onReleased event
        AbstractQueue.StoreFileListenerForDeletion storeFileListenerForDeletion = new AbstractQueue.StoreFileListenerForDeletion(this.getClass().getSimpleName());
        storeFileListenerForDeletion.setTailer(mockTailer);

        int tailerCycle = 0;
        int releasedFileCycle = 1;
        Mockito.when(mockTailer.cycle()).thenReturn(tailerCycle);
        storeFileListenerForDeletion.onReleased(releasedFileCycle, releasedFile);

        // None of the files should be deleted as the Tailer's cycle < Released file's cycle
        assertTrue(fileWithWrongExtension.exists());  // Not deleted, as the file does not have a .cq4 extension
        assertTrue(oldFile_1.exists()); // Not Deleted
        assertTrue(oldFile_2.exists()); // Not Deleted


        assertTrue(newFileWithWrongExtension.exists()); // Not deleted, as the file does not have a .cq4 extension and is accessed/updated after the released file
        assertTrue(newFile_1.exists()); // Not deleted
        assertTrue(newFile_2.exists()); // Not deleted

        assertTrue(releasedFile.exists()); // Not deleted

        assertTrue(metadataFile.exists()); // Not deleted
    }
}
