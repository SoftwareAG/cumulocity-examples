package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.snmp.integration.platform.service.MeasurementMockService;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Lazy
@RestController
@RequestMapping(MeasurementMockResource.PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeasurementMockResource {
    public static final String PATH = "/measurement/measurements";

    private final MeasurementMockService measurementMockService;
    private final PlatformProperties platformProperties;

    @RequestMapping(method = POST)
    public ResponseEntity<MeasurementRepresentation> storeMeasurement(@RequestBody final MeasurementRepresentation representation) throws URISyntaxException {
        representation.setSelf(platformProperties.getUrl() + InventoryMockResource.PATH);
        return ResponseEntity.created(new URI(representation.getSelf())).body(measurementMockService.store(representation));
    }
}
