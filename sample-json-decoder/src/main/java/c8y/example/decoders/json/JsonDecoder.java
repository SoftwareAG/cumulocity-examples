/*
 * Copyright © 2019 Software AG, Darmstadt, Germany and/or its licensors
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
 *
 */

package c8y.example.decoders.json;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DecoderInputData;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.model.idtype.GId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@MicroserviceApplication
@RequestMapping(value = "/decode")
public class JsonDecoder {



    private final JsonDecoderService jsonDecoderService;

    @Autowired
    public JsonDecoder(JsonDecoderService jsonDecoderService){
        this.jsonDecoderService = jsonDecoderService;
    }

    public static void main(String[] args) {
        SpringApplication.run(JsonDecoder.class,args);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DecoderResult decodeWithJSONInput(@RequestBody DecoderInputData inputData) {
        return jsonDecoderService.decode(inputData.getValue(), GId.asGId(inputData.getSourceDeviceId()), inputData.getArgs());
    }

}
