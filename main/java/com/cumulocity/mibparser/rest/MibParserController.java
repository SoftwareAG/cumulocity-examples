/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2019 Software AG, Darmstadt, Germany and/or its licensors
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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
@RequestMapping(value = "/mibparser/mib")
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
