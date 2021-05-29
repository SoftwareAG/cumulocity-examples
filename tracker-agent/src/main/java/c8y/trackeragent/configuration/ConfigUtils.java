/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.configuration;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;

import com.cumulocity.sdk.client.SDKException;

public class ConfigUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    
    public static final String CONFIG_FILE_NAME = "tracker-agent-server.properties";
    public static final String DEVICES_FILE_NAME = "device.properties";
    
    private static final String PLATFORM_HOST_PROP = "C8Y.baseURL";    
    private static final String FORCE_INITIAL_HOST_PROP = "C8Y.forceInitialHost";    
    private static final boolean DEFAULT_FORCE_INITIAL_HOST = false;
    private static final String LOCAL_PORT_1_PROP = "localPort1";
    private static final String DEFAULT_LOCAL_PORT_1 = "9090";
    private static final String LOCAL_PORT_2_PROP = "localPort2";
    private static final String DEFAULT_LOCAL_PORT_2 = "9091";
    private static final String BOOTSTRAP_USER_PROP = "C8Y.devicebootstrap.user";
    private static final String BOOTSTRAP_PASSWORD_PROP = "C8Y.devicebootstrap.password";
    private static final String SMS_GATEWAY_USERNAME_PROP = "C8Y.smsgateway.user";
    private static final String SMS_GATEWAY_PASSWORD_PROP = "C8Y.smsgateway.password";
    private static final String CLIENT_TIMEOUT_PROP = "client.timeout";
    private static final String COBAN_LOCATION_REPORT_INTERVAL_PROP = "coban.locationReport.timeInterval";
    private static final String RFV16_LOCATION_REPORT_INTERVAL_PROP = "rfv16.locationReport.timeInterval";
    private static final String DEFAULT_CLIENT_TIMEOUT = "" + TimeUnit.MINUTES.toMillis(5);
    private static final String NUMBER_OF_READER_WORKERS = "numberOfReaderWorkers";
    private static final Integer DEFAULT_NUMBER_OF_READER_WORKERS = 10;
    
    private static final Random random = new Random();
    private static final ConfigUtils instance = new ConfigUtils();

    
    /**
     * Path to the folder with configuration files: common.properties and device.properties
     * On production it's /etc/tracker-agent.
     * On tests it's target/test-classes.
     */
    public static ConfigUtils get() {
        return instance;
    }


    public String getConfigDir() {
        return "/etc/tracker-agent/";
    }
    
    public String getConfigFilePath(String fileName) {
        return getConfigDir() + fileName;
    }
    
    public static Properties getProperties(String path) throws SDKException {
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
        String sourceFilePath = getConfigFilePath(CONFIG_FILE_NAME);
        Properties props = getProperties(sourceFilePath);
        int clientTimeout = parseInt(getProperty(props, CLIENT_TIMEOUT_PROP, DEFAULT_CLIENT_TIMEOUT));
        //@formatter:off
        TrackerConfiguration config = new TrackerConfiguration()
            .setPlatformHost(getProperty(props, PLATFORM_HOST_PROP))
            .setForceInitialHost(getBooleanProperty(props, FORCE_INITIAL_HOST_PROP, DEFAULT_FORCE_INITIAL_HOST))
            .setLocalPort1(getLocalPort(props, LOCAL_PORT_1_PROP, DEFAULT_LOCAL_PORT_1))
            .setLocalPort2(getLocalPort(props, LOCAL_PORT_2_PROP, DEFAULT_LOCAL_PORT_2))
            .setBootstrapUser(getProperty(props, BOOTSTRAP_USER_PROP))
            .setBootstrapPassword(getProperty(props, BOOTSTRAP_PASSWORD_PROP))
            .setBootstrapTenant("management")
            .setSmsGatewayUser(getProperty(props, SMS_GATEWAY_USERNAME_PROP))
            .setSmsGatewayPassword(getProperty(props, SMS_GATEWAY_PASSWORD_PROP))
            .setCobanLocationReportTimeInterval(getIntegerProperty(props, COBAN_LOCATION_REPORT_INTERVAL_PROP, CobanConstants.DEFAULT_LOCATION_REPORT_INTERVAL))
            .setRfv16LocationReportTimeInterval(getIntegerProperty(props, RFV16_LOCATION_REPORT_INTERVAL_PROP, RFV16Constants.DEFAULT_LOCATION_REPORT_INTERVAL))
            .setClientTimeout(clientTimeout)
            .setNumberOfReaderWorkers(getIntegerProperty(props, NUMBER_OF_READER_WORKERS, DEFAULT_NUMBER_OF_READER_WORKERS));
        //@formatter:on
        logger.info(format("Configuration loaded from: %s: %s", sourceFilePath, config));
        return config;

    }
    
    private String getProperty(Properties props, String key) {
        String value = getProperty(props, key, null);
        if (value == null) {
            throw new RuntimeException("Missing property \'" + key + "\' in file " + CONFIG_FILE_NAME);
        }
        return value;
    }
    
    private String getProperty(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
    
    private Integer getIntegerProperty(Properties props, String key, Integer defaultValue) {
        return Integer.parseInt(getProperty(props, key, defaultValue.toString()));
    }
    
    private boolean getBooleanProperty(Properties props, String key, Boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(props, key, defaultValue.toString()));
    }
    
    private int getLocalPort(Properties props, String key, String defaultValue) {
        String port = getProperty(props, key, defaultValue);
        return "$random".equals(port) ? randomPort() : parseInt(port);
    }
        
    private static int randomPort() {
        return random.nextInt(20000) + 40000;
    }

}
