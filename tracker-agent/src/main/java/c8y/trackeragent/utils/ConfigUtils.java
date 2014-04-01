package c8y.trackeragent.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

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

    public String getConfigFilePath(String fileName) {
        return configDir + File.separator + fileName;
    }

    private static ConfigUtils create() {
        return new ConfigUtils(getConfigDir());
    }
    
    private static String getConfigDir() {
        Properties props = new Properties();
        InputStream io = null;
        try {
            io = TrackerContextFactory.class.getResourceAsStream("/config.properties");
            props.load(io);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(io);
        }
        return props.getProperty("configDir");
    }

}
