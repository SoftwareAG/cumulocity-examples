package c8y.trackeragent.devicebootstrap;

import static java.util.Arrays.asList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

public class DeviceCredentialsRepository {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCredentialsRepository.class);

    public static final String SOURCE_FILE = "device.properties";
    private static final DeviceCredentialsRepository instance;

    private final Map<String, DeviceCredentials> credentials = new ConcurrentHashMap<>();
    private final GroupPropertyAccessor propertyAccessor;

    static {
        instance = new DeviceCredentialsRepository();
        instance.refresh();
    }

    public static DeviceCredentialsRepository instance() {
        return instance;
    }

    private DeviceCredentialsRepository() {
        propertyAccessor = new GroupPropertyAccessor(ConfigUtils.get().getConfigFilePath(SOURCE_FILE), asList("tenantId", "user", "password"));
    }

    public boolean hasCredentials(String imei) {
        return credentials.containsKey(imei);
    }

    public DeviceCredentials getCredentials(String imei) {
        DeviceCredentials deviceCredentials = credentials.get(imei);
        if (deviceCredentials == null) {
            throw UnknownDeviceException.forImei(imei);
        }
        return deviceCredentials.duplicate();
    }

    public synchronized void saveCredentials(String imei, DeviceCredentials newCredentials) {
        Group group = asGroup(imei, newCredentials);
        if (!group.isFullyInitialized()) {
            throw new IllegalArgumentException("Not fully initialized credentials: " + newCredentials);
        }
        propertyAccessor.write(group);
        credentials.put(imei, newCredentials);
        logger.info("Credentials from device {} have been written: {}.", imei, newCredentials);
    }

    private void refresh() {
        propertyAccessor.refresh();
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