package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.snmp.integration.platform.service.EventMockService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.cumulocity.snmp.integration.platform.service.EventMockService.EVENT_PATH;

@Lazy
@RestController
@RequestMapping(value = EVENT_PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventMockResource {
    private final EventMockService service;

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity store(EventRepresentation object) {
        service.save(object);
        return ResponseEntity.created(new URI(object.getSelf())).build();
    }
}
