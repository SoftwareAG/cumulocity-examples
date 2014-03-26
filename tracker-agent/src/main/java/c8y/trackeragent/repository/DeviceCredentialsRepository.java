package c8y.trackeragent.repository;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;

import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.KeyValueDataReader;
import c8y.trackeragent.utils.KeyValueDataReader.Group;

public class DeviceCredentialsRepository {
    
    public static final String SOURCE_PATH = "/device.properties";
    
    private final static DeviceCredentialsRepository instance;
    
    private final Map<String, DeviceCredentials> credentials = new HashMap<>();
    
    static {
        instance = new DeviceCredentialsRepository();
        instance.refresh();
    }
    
    public static DeviceCredentialsRepository instance() {
        return instance;
    }
    
    private DeviceCredentialsRepository() {}

    public DeviceCredentials getCredentials(String imei) {
        DeviceCredentials deviceCredentials = credentials.get(imei);
        if(deviceCredentials == null) {
            throw UnknownDeviceException.forImei(imei);
        }
        return deviceCredentials;
    }
    
    private void refresh() {
        KeyValueDataReader dataReader = new KeyValueDataReader(SOURCE_PATH, asList("tenantId", "user", "password"));
        dataReader.init();
        for (Group group : dataReader.getGroups()) {
            if(group.isFullyInitialized()) {
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
    
}