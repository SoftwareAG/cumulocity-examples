package com.cumulocity.mibparser.service;

import com.cumulocity.mibparser.model.MibUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MibParserService {

    MibUploadResult processMibZipFile(MultipartFile file) throws IOException;
}
