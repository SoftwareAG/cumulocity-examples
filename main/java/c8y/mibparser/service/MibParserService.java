package c8y.mibparser.service;

import c8y.mibparser.model.MibUploadResult;
import net.percederberg.mibble.MibLoaderException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MibParserService {

    MibUploadResult processMibZipFile(MultipartFile file) throws IOException;
}
