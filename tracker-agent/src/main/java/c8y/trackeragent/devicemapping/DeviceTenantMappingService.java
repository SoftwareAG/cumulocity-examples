package c8y.trackeragent.devicemapping;


import c8y.trackeragent.exception.UnknownDeviceException;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class DeviceTenantMappingService {

    private final DeviceTenantMappingRepository deviceTenantMappingRepository;
    private final MicroserviceSubscriptionsService microserviceSubscriptionsService;
    private final String ownerTenant;

    @Autowired
    public DeviceTenantMappingService(DeviceTenantMappingRepository deviceTenantMappingRepository,
                                      MicroserviceSubscriptionsService microserviceSubscriptionsService,
                                      @Value("${C8Y.servicebootstrap.tenant}") String ownerTenant) {
        this.deviceTenantMappingRepository = deviceTenantMappingRepository;
        this.microserviceSubscriptionsService = microserviceSubscriptionsService;
        this.ownerTenant = ownerTenant;
    }

    public String findTenant(String externalDeviceId) {
        return microserviceSubscriptionsService.callForTenant(ownerTenant, () -> {
            Optional<String> tenantOptional = deviceTenantMappingRepository.findTenantOptional(externalDeviceId);
            if (tenantOptional.isPresent()) {
                return tenantOptional.get();
            } else {
                throw UnknownDeviceException.forImei(externalDeviceId);
            }
        });
    }

    public void addDeviceToTenant(String externalDeviceId, String tenant) {
        log.info("Started adding device: {} to tenant: {}", externalDeviceId, tenant);
        microserviceSubscriptionsService.runForTenant(ownerTenant, () -> {
            ManagedObjectRepresentation tenantObject = getOrCreateTenantManagementObjectRepresentation(tenant);
            addDeviceToExistingTenant(tenantObject, externalDeviceId);
        });
    }

    private ManagedObjectRepresentation getOrCreateTenantManagementObjectRepresentation(String tenant) {
        Optional<ManagedObjectRepresentation> optionalTenantObject = deviceTenantMappingRepository.findManagedObjectForTenant(tenant);
        return optionalTenantObject.orElseGet(() -> deviceTenantMappingRepository.createTenantObject(tenant));
    }

    private void addDeviceToExistingTenant(ManagedObjectRepresentation tenantObject, String externalDeviceId) {
        Optional<String> existingOptionalTenant = deviceTenantMappingRepository.findTenantOptional(externalDeviceId);
        if (existingOptionalTenant.isPresent()) {
            String tenantToAssign = (String) tenantObject.get(DeviceTenantMappingRepository.TENANT_FIELD);
            String existingTenant = existingOptionalTenant.get();
            if (isNeedToBeReassigned(tenantToAssign, existingTenant, externalDeviceId)) {
                log.warn("Device {} is now assigned to tenant {}. It will be reassigned to tenant {}",
                        externalDeviceId, existingTenant, tenantToAssign);
            }
        } else {
            deviceTenantMappingRepository.addDeviceToExistingTenant(externalDeviceId, tenantObject);
        }
    }

    private boolean isNeedToBeReassigned(String tenantToAssign, String existingTenant, String externalDeviceId) {
        if (StringUtils.equals(tenantToAssign, existingTenant)) {
            log.info("Not able to save because pair tenant: {} and externalId: {} already exists", existingTenant, externalDeviceId);
            return false;
        } else {
            return true;
        }
    }
}
