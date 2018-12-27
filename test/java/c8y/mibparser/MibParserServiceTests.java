package c8y.mibparser;

import c8y.mibparser.customexception.IllegalMibUploadException;
import c8y.mibparser.service.impl.MibParserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MibParserServiceTests {

	@InjectMocks
	private MibParserServiceImpl mibParserService;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerExceptionWhileProcessMibZipFile() throws IOException, IllegalMibUploadException {
		mibParserService.processMibZipFile(any(MultipartFile.class));
	}

	@Test(expected = IllegalMibUploadException.class)
	public void shouldThrowIllegalMibUploadExceptionWhenManifestFileMissing()
			throws IOException, IllegalMibUploadException {
		MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
				new File(System.getProperties().get("user.dir")+"/src/test/resources/Test_MIB_No_Manifest.zip")));
		mibParserService.processMibZipFile(file);
	}

	@Test(expected = IllegalMibUploadException.class)
	public void shouldThrowIllegalMibUploadExceptionWhenMibsMissing() throws IOException, IllegalMibUploadException {
		MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
				new File(System.getProperties().get("user.dir")+"/src/test/resources/Test_No_MIB.zip")));
		mibParserService.processMibZipFile(file);
	}

	@Test(expected = IllegalMibUploadException.class)
	public void shouldThrowIllegalMibUploadExceptionWhenNoMainMibsInManifestFile()
			throws IOException, IllegalMibUploadException {
		MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
				new File(System.getProperties().get("user.dir")+"/src/test/resources/Test_No_Main_MIB.zip")));
		mibParserService.processMibZipFile(file);
	}

	@Test(expected = IOException.class)
	public void shouldThrowIOExceptionWhenZipFileNotFound()
			throws IOException, IllegalMibUploadException {
		MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
				new File(System.getProperties().get("user.dir")+"/src/test/resources/Test.zip")));
		mibParserService.processMibZipFile(file);
	}

	@Test
	public void shouldProcessMibZipFileSuccessfully() throws IOException, IllegalMibUploadException {
		MultipartFile file = new MockMultipartFile("Test_MIB.zip", new FileInputStream(
				new File(System.getProperties().get("user.dir")+"/src/test/resources/Test_MIB.zip")));
		mibParserService.processMibZipFile(file);
	}
}
