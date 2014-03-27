package c8y.trackeragent.repository;

import static c8y.trackeragent.utils.ConfigUtils.getConfigFilePath;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

public class DeviceCredentialsRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceCredentialsRepository.class);

    public static final String SOURCE_FILE = "device.properties";
    private static final DeviceCredentialsRepository instance;

    private final Map<String, DeviceCredentials> credentials = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final GroupPropertyAccessor propertyAccessor;

    static {
        instance = new DeviceCredentialsRepository();
        instance.refresh();
    }

    public static DeviceCredentialsRepository instance() {
        return instance;
    }

    private DeviceCredentialsRepository() {
        propertyAccessor = new GroupPropertyAccessor(
                getConfigFilePath(SOURCE_FILE), asList("tenantId", "user", "password"));
    }

    public DeviceCredentials getCredentials(String imei) {
        rwLock.readLock().lock();
        try {
            DeviceCredentials deviceCredentials = credentials.get(imei);
            if (deviceCredentials == null) {
                throw UnknownDeviceException.forImei(imei);
            }
            return deviceCredentials;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public void saveCredentials(String imei, DeviceCredentials credentials) {
        rwLock.writeLock().lock();
        try {
            Group group = asGroup(imei, credentials);
            if(!group.isFullyInitialized()) {
                throw new IllegalArgumentException("Not fully initialized credentials: " + credentials);
            }
            propertyAccessor.write(group);
            logger.info("Credentials from device {} have been written: {}.", imei, credentials);
            copyGroupsToCredentials();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void refresh() {
        rwLock.writeLock().lock();
        try {
            propertyAccessor.refresh();
            copyGroupsToCredentials();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void copyGroupsToCredentials() {
        credentials.clear();
        for (Group group : propertyAccessor.getGroups()) {
            if (group.isFullyInitialized()) {
                credentials.put(group.getGroupName(), asCredentials(group));
            } 
        }
    }

    private DeviceCredentials asCredentials(Group group) {
        DeviceCredentials credentials = new DeviceCredentials();
        credentials.setTenantId(group.get("tenantId"));
        credentials.setUser(group.get("user"));
        credentials.setPassword(group.get("password"));
        return credentials;
    }
    
    private Group asGroup(String imei, DeviceCredentials credentials) {
        Group group = propertyAccessor.createEmptyGroup(imei);
        group.put("tenantId", credentials.getTenantId());
        group.put("user", credentials.getUser());
        group.put("password", credentials.getPassword());
        return group;
    }
}