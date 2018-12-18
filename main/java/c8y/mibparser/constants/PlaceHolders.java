package c8y.mibparser.constants;

public interface PlaceHolders {

    String HOME_DIR = "user.home";
    String TEMP_DIR_NAME = "mib-zip-tmp-dir-";
    String MANIFEST_FILENAME = "mib-index.txt";
    String REQUEST_PARAM_NAME = "file";
    String FIELD_BUS_TYPE = "snmp";

//    Exception Messages
    String MISSING_MIB_DEPENDENCIES = "MIB dependent files are missing";
    String DIR_NOT_ALLOWED = "Directories are not allowed inside zip file";
    String NO_MANIFEST_FILE_FOUND = "Zip file does not contain "+MANIFEST_FILENAME;
}
