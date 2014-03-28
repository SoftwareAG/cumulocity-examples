package c8y.trackeragent.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigUtils {
    
    private static final ConfigUtils instance = create();
    
    
    /**
     * Path to the folder with configuration files: common.properties and device.properties
     * On production it's /etc/tracker-agent.
     * On tests it's target/test-classes.
     */
    private final String configDir;
    
    private ConfigUtils(String configDir) {
        this.configDir = configDir;
    }
    
    public static ConfigUtils get() {
        return instance;
    }

    public Path getConfigFilePath(String fileName) {
        return FileSystems.getDefault().getPath(configDir, fileName);
    }

    private static ConfigUtils create() {
        return new ConfigUtils(getConfigDir());
    }
    
    private static String getConfigDir() {
        Properties props = new Properties();
        try (InputStream io = TrackerContextFactory.class.getResourceAsStream("/config.properties")) {
            props.load(io);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props.getProperty("configDir");
    }

}
