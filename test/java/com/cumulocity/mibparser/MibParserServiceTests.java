package com.cumulocity.mibparser;

import com.cumulocity.mibparser.conversion.RegisterConversionHandler;
import com.cumulocity.mibparser.model.MibUploadResult;
import com.cumulocity.mibparser.model.Register;
import com.cumulocity.mibparser.service.impl.MibParserServiceImpl;
import net.percederberg.mibble.MibValueSymbol;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MibParserServiceTests {

    @Mock
    private RegisterConversionHandler handler;

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
        MibValueSymbol mibValueSymbol = mock(MibValueSymbol.class);
        Register register = mock(Register.class);

        when(handler.convertSnmpObjectToRegister(mibValueSymbol)).thenReturn(register);

        MibUploadResult mibUploadResult = mibParserService.processMibZipFile(file);

        Assert.assertNotNull(mibUploadResult);
    }
}
