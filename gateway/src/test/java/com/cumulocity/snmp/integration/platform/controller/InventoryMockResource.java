package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.integration.notification.RealtimeBroadcaster;
import com.cumulocity.snmp.integration.platform.service.InventoryMockService;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Lazy
@RestController
@RequestMapping(InventoryMockResource.PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryMockResource {

    public static final String PATH = "/inventory/managedObjects";

    private final InventoryMockService inventoryMockService;
    private final PlatformProperties platformProperties;
    private final RealtimeBroadcaster realtimeBroadcaster;

    @RequestMapping(method = POST)
    public ResponseEntity<ManagedObjectRepresentation> storeManagedObjects(@RequestBody final ManagedObjectRepresentation representation) throws URISyntaxException {
        representation.setSelf(platformProperties.getUrl() + InventoryMockResource.PATH);
        final ManagedObjectRepresentation savedManagedObject = inventoryMockService.store(representation);
        return ResponseEntity.created(new URI(savedManagedObject.getSelf())).body(savedManagedObject);
    }

    @RequestMapping(method = PUT, value = "/{id}")
    public ResponseEntity<?> updateManagedObjects(@PathVariable("id") final String id,
                                                  @RequestBody final ManagedObjectRepresentation representation) throws URISyntaxException {
        if (inventoryMockService.findById(GId.asGId(id)).isPresent()) {
            final ManagedObjectRepresentation updatesManagedObject = inventoryMockService.update(GId.asGId(id), representation);
            realtimeBroadcaster.sendUpdate(updatesManagedObject);
            return ResponseEntity.ok(updatesManagedObject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(method = GET)
    public ResponseEntity<ManagedObjectCollectionRepresentation> findByType(@RequestParam("type") final String type) throws URISyntaxException {
        return ResponseEntity.ok(inventoryMockService.findByType(type));
    }

    @RequestMapping(method = GET, value = "/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") final String id) {
        final Optional<ManagedObjectRepresentation> representationOptional = inventoryMockService.findById(GId.asGId(id));
        if (representationOptional.isPresent()) {
            return ResponseEntity.ok(representationOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(method = POST, value = "/{id}/childDevices")
    public ResponseEntity<ManagedObjectReferenceRepresentation> addChildDevice(@PathVariable("id") final String id,
                                                                               @RequestBody final ManagedObjectReferenceRepresentation children) throws URISyntaxException {
        children.setSelf(platformProperties.getUrl() + InventoryMockResource.PATH + "/" + id + "/childDevices");
        inventoryMockService.addChildDevice(GId.asGId(id), children);
        return ResponseEntity.created(new URI(children.getSelf())).body(children);
    }

    @RequestMapping(method = GET, value = "/{id}/childDevices")
    public ResponseEntity<?> getChildDevice(@PathVariable("id") final String id) throws URISyntaxException {
        final Optional<ManagedObjectRepresentation> rootDevice = inventoryMockService.findById(GId.asGId(id));
        if (rootDevice.isPresent()) {
            final ManagedObjectReferenceCollectionRepresentation representations = new ManagedObjectReferenceCollectionRepresentation();
            representations.setReferences(rootDevice.get().getChildDevices().getReferences());
            return ResponseEntity.ok(representations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
