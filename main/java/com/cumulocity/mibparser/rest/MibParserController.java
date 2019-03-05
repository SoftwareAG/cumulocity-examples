package com.cumulocity.mibparser.rest;

import com.cumulocity.mibparser.model.MibUploadResult;
import com.cumulocity.mibparser.service.MibParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

import static com.cumulocity.mibparser.constants.Constants.REQUEST_PARAM_NAME;

@Slf4j
@RestController
@RequestMapping(value = "/mib")
public class MibParserController {

    @Autowired
    private MibParserService mibParserService;

    @RequestMapping(value = "/uploadzip",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MibUploadResult mibZipUpload(@RequestParam(REQUEST_PARAM_NAME) @NotNull final MultipartFile file)
            throws Exception {
        log.info("Received MIB Zip file: " + file.getOriginalFilename());
        return mibParserService.processMibZipFile(file);
    }
}
