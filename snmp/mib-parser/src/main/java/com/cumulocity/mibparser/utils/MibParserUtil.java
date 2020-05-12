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

package com.cumulocity.mibparser.utils;

import static com.cumulocity.mibparser.utils.Constants.TMPDIR;
import static com.cumulocity.mibparser.utils.Constants.TEMP_DIR_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MibParserUtil {

    public static File createTempDirectory(String dirPath) {
        File file = new File(dirPath);
        boolean folderCreated = file.mkdir();

        if(!folderCreated) {
            log.error("Unable to create the directory {}", dirPath);
        }

        return file;
    }

    public static String getTempDirectoryPath(String filename, String tenant, String username) {
        StringBuilder builder = new StringBuilder()
                .append(System.getProperty(TMPDIR))
                .append(File.separator)
                .append(TEMP_DIR_NAME)
                .append(tenant).append("-")
                .append(username).append("-")
                .append(filename).append("-")
                .append(System.nanoTime());

        return builder.toString();
    }

    public static List<String> readMainFile(File file) throws IOException {
        return Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);
    }
}
