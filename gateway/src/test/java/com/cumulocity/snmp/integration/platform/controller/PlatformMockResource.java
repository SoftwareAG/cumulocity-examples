package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.rest.representation.alarm.AlarmCollectionRepresentation;
import com.cumulocity.rest.representation.alarm.AlarmsApiRepresentation;
import com.cumulocity.rest.representation.identity.IdentityRepresentation;
import com.cumulocity.rest.representation.inventory.InventoryRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementCollectionRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementsApiRepresentation;
import com.cumulocity.rest.representation.operation.DeviceControlRepresentation;
import com.cumulocity.rest.representation.operation.OperationCollectionRepresentation;
import com.cumulocity.rest.representation.platform.PlatformApiRepresentation;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cumulocity.snmp.integration.platform.service.AlarmMockService.ALARM_PATH;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Lazy
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformMockResource {

    private final PlatformProperties platformProperties;

    @RequestMapping(value = "/platform", method = GET)
    public ResponseEntity<PlatformApiRepresentation> getPlatform() {
        final PlatformApiRepresentation response = new PlatformApiRepresentation();
        response.setInventory(inventory());
        response.setIdentity(identity());
        response.setMeasurement(measurement());
        response.setDeviceControl(deviceControl());
        response.setAlarm(alarm());
        return ResponseEntity.ok(response);
    }

    private AlarmsApiRepresentation alarm() {
        final AlarmCollectionRepresentation alarms = new AlarmCollectionRepresentation();
        alarms.setSelf(platformProperties.getUrl() + ALARM_PATH);

        final AlarmsApiRepresentation result = new AlarmsApiRepresentation();
        result.setAlarms(alarms);
        return result;
    }

    private IdentityRepresentation identity() {
        final IdentityRepresentation identity = new IdentityRepresentation();
        identity.setExternalId(platformProperties.getUrl() + IdentityMockResource.EXTERNAL_ID_PATH);
        identity.setExternalIdsOfGlobalId(platformProperties.getUrl() + IdentityMockResource.EXTERNAL_ID_OF_GLOBAL_PATH);
        return identity;
    }

    private InventoryRepresentation inventory() {
        final InventoryRepresentation inventory = new InventoryRepresentation();
        inventory.setSelf(platformProperties.getUrl() + InventoryMockResource.PATH);
        inventory.setManagedObjectsForFragmentType(platformProperties.getUrl() + InventoryMockResource.PATH + "?fragmentType={fragmentType}");
        inventory.setManagedObjects(managedObjects());
        return inventory;
    }

    private MeasurementsApiRepresentation measurement() {
        final MeasurementCollectionRepresentation measurements = new MeasurementCollectionRepresentation();
        measurements.setSelf(platformProperties.getUrl() + MeasurementMockResource.PATH);

        final MeasurementsApiRepresentation representation = new MeasurementsApiRepresentation();
        representation.setSelf(platformProperties.getUrl() + MeasurementMockResource.PATH);
        representation.setMeasurements(measurements);
        return representation;
    }

    private DeviceControlRepresentation deviceControl() {
        final DeviceControlRepresentation representation = new DeviceControlRepresentation();
        representation.setSelf(platformProperties.getUrl() + DeviceControlMockResource.PATH);

        final OperationCollectionRepresentation operations = new OperationCollectionRepresentation();
        operations.setSelf(platformProperties.getUrl() + DeviceControlMockResource.PATH + "/operations");
        representation.setOperations(operations);
        return representation;
    }

    private ManagedObjectReferenceCollectionRepresentation managedObjects() {
        final ManagedObjectReferenceCollectionRepresentation result = new ManagedObjectReferenceCollectionRepresentation();
        result.setSelf(platformProperties.getUrl() + InventoryMockResource.PATH);
        return result;
    }
}
