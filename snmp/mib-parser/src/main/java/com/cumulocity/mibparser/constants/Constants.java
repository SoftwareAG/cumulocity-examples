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

package com.cumulocity.mibparser.constants;

public class Constants {

    public static final String TMPDIR = "java.io.tmpdir";
    public static final String TEMP_DIR_NAME = "mib-tmpdir-";
    public static final String MANIFEST_FILENAME = "mib-index.txt";
    public static final String REQUEST_PARAM_NAME = "file";
    public static final String FIELD_BUS_TYPE = "snmp";

    //    Exception Messages
    public static final String MISSING_MIB_DEPENDENCIES = "MIB dependent files are missing";
    public static final String DIR_NOT_ALLOWED = "Directories are not allowed inside Zip file";
    public static final String NO_MANIFEST_FILE_FOUND = "Zip file does not contain " + MANIFEST_FILENAME;
    public static final String NO_MAIN_MIBS_FOUND = MANIFEST_FILENAME + " is empty in Zip file";
    public static final String NO_MIB_FOUND_IN_ZIP_FILE = "No MIBs found in Zip file";
    public static final String NO_MIB_INFO_FOUND_IN_ZIP_FILE = "No MIB information found in Zip file";
    public static final String IO_EXCEPTION_DURING_ZIP_FILE_PROCESSING = "Exception while processing received Zip file";
    public static final String FAILED_TO_DELETE_TMP_DIR = "Unable to delete tmp directory while processing MIB files";

}
