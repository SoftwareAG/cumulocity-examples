package c8y.migration.steps;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import c8y.migration.model.TenantMigrationRequest;
import c8y.migration.model.TenantMigrationResponse;
import c8y.migration.service.MigrationResponseSerializer;

@Component
@Order(value = 30)
public class SerializeTenanCredentialsStep extends MigrationStep {

	@Override
	public void execute(TenantMigrationRequest req, TenantMigrationResponse response) {
		String otputFileName = "device-" + response.getTenant() + ".properties";
		new MigrationResponseSerializer(otputFileName).serialize(response);
	}
	
	

}
