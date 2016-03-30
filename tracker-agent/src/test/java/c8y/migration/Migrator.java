package c8y.migration; 

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Migrator {
	
	private static final Logger logger = LoggerFactory.getLogger(Migrator.class);
	
	private final MigrationRequestService requestService;

	@Autowired
	public Migrator(MigrationRequestService migrationRequestService) {
		this.requestService = migrationRequestService;
	}
	
	@PostConstruct
	public void init() {
		List<TenantMigrationRequest> reqs = requestService.getAll();
		logger.info("Migrator requests: {}", reqs);
	}
	
	
}
