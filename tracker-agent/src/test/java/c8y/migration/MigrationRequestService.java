package c8y.migration;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

@Component
public class MigrationRequestService {
	
	private static final Logger logger = LoggerFactory.getLogger(MigrationRequestService.class);
	
	private GroupPropertyAccessor propertyAccessor;

	public MigrationRequestService() {
		propertyAccessor = new GroupPropertyAccessor("/etc/tracker-agent-migration/device.properties", asList("tenantId"));
	}

	public List<TenantMigrationRequest> getAll() {
		propertyAccessor.refresh();
		Map<String, TenantMigrationRequest> index = new LinkedHashMap<String, TenantMigrationRequest>();
		for (Group group : propertyAccessor.getGroups()) {
			String tenant = group.get("tenantId");
			index.put(tenant, new TenantMigrationRequest(tenant));
		}
		for (Group group : propertyAccessor.getGroups()) {
			String tenant = group.get("tenantId");
			String imei = group.getName();
			TenantMigrationRequest request = index.get(tenant);
			request.add(new DeviceMigrationRequest(imei));
		}
		return new ArrayList<>(index.values());
	}

}
