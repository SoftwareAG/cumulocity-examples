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

package com.cumulocity.mibparser;

import com.cumulocity.mibparser.service.MibParserService;
import com.cumulocity.mibparser.service.RegisterConversionService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(value = "/application.properties", properties = {"logging.config="})
public class MibParserServiceTest {

    @Spy
    private RegisterConversionService registerConversionService;

    @InjectMocks
    private MibParserService mibParserService;

    private String tenant = "testtenant";

    private String username = "testusername";

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionWhileProcessMibZipFile() throws IOException, IllegalArgumentException {
        mibParserService.processMibZipFile(mock(MultipartFile.class), tenant, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenManifestFileMissing()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB_No_Manifest.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenMibsMissing() throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_No_MIB.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNoMainMibsInManifestFile()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_No_Main_MIB.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenZipFileNotFound() throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNoDependentMibsAvailable()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB_With_No_Dependencies", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB_With_No_Dependencies.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenZipWithFolderUploaded()
            throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB_With_Folder", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB_With_Folder.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }

    @Test
    public void shouldProcessMibZipFileSuccessfully() throws IOException, IllegalArgumentException {
        MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
                new File(System.getProperties().get("user.dir") + "/src/test/resources/Test_MIB.zip")));
        mibParserService.processMibZipFile(file, tenant, username);
    }
}
