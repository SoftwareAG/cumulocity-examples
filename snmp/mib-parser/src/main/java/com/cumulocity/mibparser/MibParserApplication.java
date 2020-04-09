/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
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

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@MicroserviceApplication
@EnableAutoConfiguration
@PropertySources(value = {
        @PropertySource(value = "file:${user.home}/.mibparser/mibparser.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/etc/mibparser/mibparser.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:META-INF/spring/mibparser.properties", ignoreResourceNotFound = true)
})
public class MibParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MibParserApplication.class, args);
    }
}