package c8y.mibparser.rest;

import c8y.mibparser.model.MibUploadResult;
import c8y.mibparser.service.MibParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static c8y.mibparser.constants.Constants.REQUEST_PARAM_NAME;

@Slf4j
@RestController
@RequestMapping(value = "/mibparser")
public class MibParserController {

    @Autowired
    private MibParserService mibParserService;

    @RequestMapping(value = "/uploadzip",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MibUploadResult mibZipUpload(@RequestParam(REQUEST_PARAM_NAME) final MultipartFile file) throws Exception {
        return mibParserService.processMibZipFile(file);
    }
}
