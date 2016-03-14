package c8y.trackeragent.devicebootstrap;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

@Component
public class DeviceCredentialsRepository {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCredentialsRepository.class);

    private final Map<String, DeviceCredentials> deviceCredentials = new ConcurrentHashMap<String, DeviceCredentials>();
    private final Map<String, DeviceCredentials> agentCredentials = new ConcurrentHashMap<String, DeviceCredentials>();
    private final GroupPropertyAccessor devicePropertyAccessor;
    private final GroupPropertyAccessor agentPropertyAccessor;
    private final Object lock = new Object();


    public DeviceCredentialsRepository(String devicePropertiesPath) {
    	devicePropertyAccessor = new GroupPropertyAccessor(devicePropertiesPath, asList("tenantId"));
    	agentPropertyAccessor = new GroupPropertyAccessor(devicePropertiesPath, asList("user", "password"));
    	
    }
    public DeviceCredentialsRepository() {
    	this(ConfigUtils.get().getConfigFilePath(ConfigUtils.DEVICES_FILE_NAME));
    }

    public boolean hasDeviceCredentials(String imei) {
        return deviceCredentials.containsKey(imei);
    }
    
	public boolean hasAgentCredentials(String imei) {
		// TODO Auto-generated method stub
		return null;
	}


    public DeviceCredentials getDeviceCredentials(String imei) {
        DeviceCredentials result = deviceCredentials.get(imei);
        if (result == null) {
            throw UnknownDeviceException.forImei(imei);
        }
        return result.duplicate();
    }
    
    public List<DeviceCredentials> getAllDeviceCredentials() {
        return new ArrayList<DeviceCredentials>(deviceCredentials.values());
    }

    public void saveDeviceCredentials(DeviceCredentials newCredentials) {
        synchronized (lock) {
            Group group = asDeviceGroup(newCredentials.getImei(), newCredentials);
            if (!group.isFullyInitialized()) {
                throw new IllegalArgumentException("Not fully initialized credentials: " + newCredentials);
            }
            devicePropertyAccessor.write(group);
            deviceCredentials.put(newCredentials.getImei(), newCredentials);
            logger.info("Credentials for device {} have been written: {}.", newCredentials.getImei(), newCredentials);
        }
    }
    
    public void saveAgentCredentials(DeviceCredentials newCredentials) {
    	synchronized (lock) {
    		Group group = asAgentGroup(newCredentials.getTenant(), newCredentials);
    		if (!group.isFullyInitialized()) {
    			throw new IllegalArgumentException("Not fully initialized credentials: " + newCredentials);
    		}
    		agentPropertyAccessor.write(group);
    		agentCredentials.put(newCredentials.getTenant(), newCredentials);
    		logger.info("Credentials for agent of tenant {} have been written: {}.", newCredentials.getTenant(), newCredentials);
    	}
    }

    @PostConstruct
    public void refresh() {
        devicePropertyAccessor.refresh();
        deviceCredentials.clear();
        for (Group group : devicePropertyAccessor.getGroups()) {
            if (group.isFullyInitialized()) {
                deviceCredentials.put(group.getName(), asDeviceCredentials(group));
            }
        }
    }

    private DeviceCredentials asDeviceCredentials(Group group) {
    	return DeviceCredentials.forDevice(group.getName(), group.get("tenantId"));
    }

    private Group asDeviceGroup(String imei, DeviceCredentials credentials) {
        Group group = devicePropertyAccessor.createEmptyGroup(imei);
        group.put("tenantId", credentials.getTenant());
        return group;
    }
    
    private Group asAgentGroup(String tenant, DeviceCredentials credentials) {
    	Group group = agentPropertyAccessor.createEmptyGroup(tenant);
    	group.put("user", credentials.getUsername());
    	group.put("password", credentials.getPassword());
    	return group;
    }
}