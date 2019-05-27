package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.snmp.integration.platform.service.DeviceControlService;
import com.cumulocity.snmp.integration.platform.service.OperationMockService;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Lazy
@RestController
@RequestMapping(DeviceControlMockResource.PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceControlMockResource extends WebSecurityConfigurerAdapter {

    public static final String PATH = "/devicecontrol";

    private final DeviceControlService deviceControlService;
    private final PlatformProperties platformProperties;
    private final OperationMockService operationMockService;

    @RequestMapping(method = POST, value = "/deviceCredentials")
    public ResponseEntity<?> pollCredentials(@RequestBody final DeviceCredentialsRepresentation deviceId) throws URISyntaxException {
        Optional<DeviceCredentialsRepresentation> representation = deviceControlService.pollCredentials(deviceId.getId());
        if (representation.isPresent()) {
            representation.get().setSelf(platformProperties.getUrl() + DeviceControlMockResource.PATH);
            return ResponseEntity.created(new URI(representation.get().getSelf())).body(representation.get());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = POST, value = "/newDeviceRequests")
    public ResponseEntity<NewDeviceRequestRepresentation> registerDevice(String id) throws URISyntaxException {
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setId(id);
        representation.setSelf(platformProperties.getUrl() + DeviceControlMockResource.PATH);
        return ResponseEntity.created(new URI(representation.getSelf())).body(representation);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
        http.csrf().disable();
    }

    @RequestMapping(method = POST, value = "/operations")
    public ResponseEntity<OperationRepresentation> saveOperation(@RequestBody final OperationRepresentation representation) throws URISyntaxException {
        representation.setSelf(platformProperties.getUrl() + DeviceControlMockResource.PATH + "/operations");
        final OperationRepresentation storedOperation = operationMockService.store(representation);
        return ResponseEntity.created(new URI(storedOperation.getSelf())).body(storedOperation);
    }

    @RequestMapping(method = GET, value = "/operations/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        Optional<OperationRepresentation> byId = operationMockService.findById(new GId(id));
        if (byId.isPresent()) {
            return ResponseEntity.ok(byId.get());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = PUT, value = "/operations/{id}")
    public ResponseEntity<?> updateOperation(@PathVariable("id") final String id, @RequestBody final OperationRepresentation representation) {
        final Optional<OperationRepresentation> updatedOptional = operationMockService.update(id, representation);
        if (updatedOptional.isPresent()) {
            return ResponseEntity.ok(updatedOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

