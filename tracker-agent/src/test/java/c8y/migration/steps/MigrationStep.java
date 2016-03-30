package c8y.migration.steps;

import c8y.migration.model.TenantMigrationRequest;
import c8y.migration.model.TenantMigrationResponse;

public abstract class MigrationStep {

	public abstract void execute(TenantMigrationRequest req, TenantMigrationResponse response);
	
}
