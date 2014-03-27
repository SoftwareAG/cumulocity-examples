package c8y.trackeragent.utils;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ConfigUtils {
    
    public static final String CONFIG_DIR_NAME = ".trackeragent";

    public static Path getConfigFilePath(String fileName) {
        String home = System.getProperty("user.home");
        return FileSystems.getDefault().getPath(home, CONFIG_DIR_NAME, fileName);
    }

}
