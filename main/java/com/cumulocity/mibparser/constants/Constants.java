/*
 * Copyright (c) 2019 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cumulocity.mibparser.constants;

public class Constants {

    public static final String TMPDIR = "java.io.tmpdir";
    public static final String TEMP_DIR_NAME = "mib-zip-tmp-dir-";
    public static final String MANIFEST_FILENAME = "mib-index.txt";
    public static final String REQUEST_PARAM_NAME = "file";
    public static final String FIELD_BUS_TYPE = "snmp";

//    Exception Messages
    public static final String MISSING_MIB_DEPENDENCIES = "MIB dependent files are missing";
    public static final String DIR_NOT_ALLOWED = "Directories are not allowed inside Zip file";
    public static final String NO_MANIFEST_FILE_FOUND = "Zip file does not contain "+MANIFEST_FILENAME;
    public static final String NO_MAIN_MIBS_FOUND = MANIFEST_FILENAME + " is empty in Zip file";
    public static final String NO_MIB_FOUND_IN_ZIP_FILE = "No MIBs found in Zip file";
    public static final String NO_MIB_INFO_FOUND_IN_ZIP_FILE = "No MIB information found in Zip file";

}
