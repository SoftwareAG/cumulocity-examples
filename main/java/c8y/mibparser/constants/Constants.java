package c8y.mibparser.constants;

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
    public static final String NO_TRAPS_FOUND_IN_ZIP_FILE = "No Traps information found in Zip file";

}
