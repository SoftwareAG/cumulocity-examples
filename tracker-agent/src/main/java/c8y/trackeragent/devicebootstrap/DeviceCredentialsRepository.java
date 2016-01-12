package c8y.trackeragent.devicebootstrap;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
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

    private static final DeviceCredentialsRepository instance;

    private final Map<String, DeviceCredentials> credentials = new ConcurrentHashMap<String, DeviceCredentials>();
    private final GroupPropertyAccessor propertyAccessor;
    private final Object lock = new Object();

    static {
        instance = new DeviceCredentialsRepository();
        instance.refresh();
    }

    public static DeviceCredentialsRepository get() {
        return instance;
    }

    private DeviceCredentialsRepository() {
        propertyAccessor = new GroupPropertyAccessor(ConfigUtils.get().getConfigFilePath(ConfigUtils.DEVICES_FILE_NAME), asList("tenantId", "user", "password"));
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
    
    public List<DeviceCredentials> getAllCredentials() {
        return new ArrayList<DeviceCredentials>(credentials.values());
    }

    public void saveCredentials(DeviceCredentials newCredentials) {
        synchronized (lock) {
            Group group = asGroup(newCredentials.getImei(), newCredentials);
            if (!group.isFullyInitialized()) {
                throw new IllegalArgumentException("Not fully initialized credentials: " + newCredentials);
            }
            propertyAccessor.write(group);
            credentials.put(newCredentials.getImei(), newCredentials);
            logger.info("Credentials for device {} have been written: {}.", newCredentials.getImei(), newCredentials);
        }
    }

    private void refresh() {
        propertyAccessor.refresh();
        credentials.clear();
        for (Group group : propertyAccessor.getGroups()) {
            if (group.isFullyInitialized()) {
                credentials.put(group.getName(), asCredentials(group));
            }
        }
    }

    private DeviceCredentials asCredentials(Group group) {
        DeviceCredentials credentials = new DeviceCredentials(group.get("tenantId"), group.get("user"), group.get("password"), null, null);
        credentials.setImei(group.getName());
        return credentials;
    }

    private Group asGroup(String imei, DeviceCredentials credentials) {
        Group group = propertyAccessor.createEmptyGroup(imei);
        group.put("tenantId", credentials.getTenant());
        group.put("user", credentials.getUsername());
        group.put("password", credentials.getPassword());
        return group;
    }
}