package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.snmp.integration.platform.service.AlarmMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import static com.cumulocity.model.idtype.GId.asGId;
import static com.cumulocity.snmp.integration.platform.service.AlarmMockService.ALARM_PATH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Lazy
@RestController
@RequestMapping(value = ALARM_PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AlarmsMockResource {

    private final AlarmMockService service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity store(@RequestBody AlarmRepresentation object) {
        AlarmRepresentation stored = service.save(object);
        ResponseEntity<AlarmRepresentation> result = null;
        try {
            result = ResponseEntity.created(new URI(stored.getSelf())).body(stored);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(method = PUT, value = "/{id}")
    public ResponseEntity update(@PathVariable("id") String id, @RequestBody AlarmRepresentation object) {
        final AlarmRepresentation updated = service.update(asGId(id), object);
        return ResponseEntity.ok(updated);
    }
}
