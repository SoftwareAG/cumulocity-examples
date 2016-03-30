package c8y.migration.service;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import c8y.migration.model.DeviceMigrationRequest;
import c8y.migration.model.TenantMigrationRequest;
import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

@Component
public class MigrationRequestService {

	private GroupPropertyAccessor propertyAccessor;

	public MigrationRequestService() {
		propertyAccessor = new GroupPropertyAccessor("/etc/tracker-agent-migration/device.properties",
				asList("tenantId", "user", "password"));
	}

	public List<TenantMigrationRequest> getAll() {
		return getAll(Predicates.<String>alwaysTrue());
	}

	public List<TenantMigrationRequest> getAll(final Collection<String> tenants) {
		return getAll(new Predicate<String>() {

			@Override
			public boolean apply(String input) {
				return tenants.contains(input);
			}
		});
	}
	
	private List<TenantMigrationRequest> getAll(Predicate<String> tenantAcceptor) {
		propertyAccessor.refresh();
		Map<String, TenantMigrationRequest> index = new LinkedHashMap<String, TenantMigrationRequest>();
		for (Group group : propertyAccessor.getGroups()) {
			String tenant = group.get("tenantId");
			if (!tenantAcceptor.apply(tenant)) {
				continue;
			}
			index.put(tenant, new TenantMigrationRequest(tenant));
		}
		for (Group group : propertyAccessor.getGroups()) {
			String tenant = group.get("tenantId");
			if (!tenantAcceptor.apply(tenant)) {
				continue;
			}
			String imei = group.getName();
			String user = group.get("user");
			String password = group.get("password");
			TenantMigrationRequest request = index.get(tenant);
			request.add(new DeviceMigrationRequest(imei, user, password));
		}
		return new ArrayList<>(index.values());
	}

}
