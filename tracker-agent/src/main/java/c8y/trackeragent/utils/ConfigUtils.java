package c8y.trackeragent.utils;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

public class ConfigUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    
    private static final String SOURCE_FILE = "common.properties";
    private static final String PLATFORM_HOST_PROP = "platformHost";    
    private static final String LOCAL_SOCKET_PORT_PROP = "localPort";
    private static final String DEFAULT_LOCAL_SOCKET_PORT = "9090";
    private static final String BOOTSTRAP_USER_PROP = "bootstrap.user";
    private static final String BOOTSTRAP_PASSWORD_PROP = "bootstrap.password";
    
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
    
    public Properties getProperties(String path) throws SDKException {
        Properties source = new Properties();
        InputStream io = null;
        try {
            io = new FileInputStream(path);
            source.load(io);
            return source;
        } catch (IOException ioex) {
            throw new SDKException("Can't load configuration from file system " + path, ioex);
        } finally {
            IOUtils.closeQuietly(io);
        }
    }
    
    public TrackerConfiguration loadCommonConfiguration() {
        String sourceFilePath = getConfigFilePath(SOURCE_FILE);
        Properties props = getProperties(sourceFilePath);
        int port = parseInt(getProperty(props, LOCAL_SOCKET_PORT_PROP, DEFAULT_LOCAL_SOCKET_PORT));
        //@formatter:off
        TrackerConfiguration config = new TrackerConfiguration()
            .setPlatformHost(getProperty(SOURCE_FILE, props, PLATFORM_HOST_PROP))
            .setLocalPort(port)
            .setBootstrapUser(getProperty(SOURCE_FILE, props, BOOTSTRAP_USER_PROP))
            .setBootstrapPassword(getProperty(SOURCE_FILE, props, BOOTSTRAP_PASSWORD_PROP))
            .setBootstrapTenant("management");
        //@formatter:on
        logger.info(format("Configuration loaded from: %s: %s", sourceFilePath, config));
        return config;

    }

    private static ConfigUtils create() {
        return new ConfigUtils(getConfigDir());
    }
    
    private static String getConfigDir() {
        Properties props = new Properties();
        InputStream io = null;
        try {
            io = ConfigUtils.class.getResourceAsStream("/config.properties");
            props.load(io);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(io);
        }
        return props.getProperty("configDir");
    }
    

    private String getProperty(String path, Properties props, String key) {
        String value = getProperty(props, key, null);
        if (value == null) {
            throw new RuntimeException("Missing property \'" + key + "\' in file " + SOURCE_FILE);
        }
        return value;
    }
    
    private String getProperty(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

}
