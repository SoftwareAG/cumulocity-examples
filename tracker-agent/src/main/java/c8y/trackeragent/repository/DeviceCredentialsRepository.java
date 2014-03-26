package c8y.trackeragent.repository;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;

import c8y.trackeragent.repository.KeyValueDataReader.Group;

public class DeviceCredentialsRepository {
    
    public static final String SOURCE_PATH = "/device-credentials.properties";
    
    private final static DeviceCredentialsRepository instance;
    
    private final Map<String, Credentials> credentials = new HashMap<>();
    
    static {
        instance = new DeviceCredentialsRepository();
        instance.refresh();
    }
    
    public static DeviceCredentialsRepository instance() {
        return instance;
    }
    
    private DeviceCredentialsRepository() {}

    public Credentials getCredentials(String imei) {
        return credentials.get(imei);
    }
    
    private void refresh() {
        KeyValueDataReader dataReader = new KeyValueDataReader(SOURCE_PATH, asList("tenantId", "login", "password"));
        dataReader.init();
        for (Group group : dataReader.getGroups()) {
            if(group.isFullyInitialized()) {
                credentials.put(group.getGroupName(), asCredentials(group));
            }
        }
    }

    private Credentials asCredentials(Group group) {
        Credentials credentials = new Credentials();
        credentials.setTenantId(group.get("tenantId"));
        credentials.setLogin(group.get("login"));
        credentials.setPassword(group.get("password"));
        return credentials;
    }
    
}
