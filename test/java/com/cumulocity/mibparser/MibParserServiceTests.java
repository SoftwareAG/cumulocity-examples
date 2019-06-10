/*
 * Copyright (c) 2019 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cumulocity.mibparser;

import com.cumulocity.mibparser.service.impl.MibParserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MibParserServiceTests {

    @InjectMocks
    private MibParserServiceImpl mibParserService;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionWhileProcessMibZipFile() throws IOException, IllegalArgumentException {
        mibParserService.processMibZipFile(mock(MultipartFile.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenManifestFileMissing()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB_No_Manifest.zip")));
        mibParserService.processMibZipFile(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenMibsMissing() throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_No_MIB.zip")));
        mibParserService.processMibZipFile(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNoMainMibsInManifestFile()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_No_Main_MIB.zip")));
        mibParserService.processMibZipFile(file);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenZipFileNotFound() throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test.zip")));
        mibParserService.processMibZipFile(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNoDependentMibsAvailable()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB_With_No_Dependencies", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB_With_No_Dependencies.zip")));
        mibParserService.processMibZipFile(file);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenZipWithFolderUploaded()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB_With_Folder", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB_With_Folder.zip")));
        mibParserService.processMibZipFile(file);
    }

    @Test
    public void shouldProcessMibZipFileSuccessfully() throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB.zip")));
        mibParserService.processMibZipFile(file);
    }
}
