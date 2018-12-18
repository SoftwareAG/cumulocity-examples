package c8y.mibparser.service;

import c8y.mibparser.customexception.IllegalMibUploadException;
import net.percederberg.mibble.MibLoaderException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MibParserService {

    String processMibZipFile(MultipartFile file) throws IOException, MibLoaderException, IllegalMibUploadException;
}
