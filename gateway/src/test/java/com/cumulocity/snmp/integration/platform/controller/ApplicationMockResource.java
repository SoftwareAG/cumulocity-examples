package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.rest.representation.application.ApplicationCollectionRepresentation;
import com.cumulocity.rest.representation.application.ApplicationRepresentation;
import com.cumulocity.rest.representation.application.ApplicationUserCollectionRepresentation;
import com.cumulocity.rest.representation.application.ApplicationUserRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/application")
public class ApplicationMockResource {

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, ApplicationRepresentation> store = new HashMap<>();

    @RequestMapping(method = GET, value = "/applicationsByName/{name}")
    public ApplicationCollectionRepresentation get(@PathVariable("name") final String name) {
        final ApplicationCollectionRepresentation result = new ApplicationCollectionRepresentation();
        result.setApplications(Lists.<ApplicationRepresentation>newArrayList());
        ApplicationRepresentation byName = findByName(name);
        if (byName != null) {
            result.getApplications().add(byName);
        }
        return result;
    }

    @RequestMapping(method = POST, value = "/applications")
    public ResponseEntity post(@RequestBody final ApplicationRepresentation representation) {
        representation.setId(randomNumeric(3));
        store.put(representation.getId(), representation);
        return ResponseEntity.status(HttpStatus.CREATED).body(representation);
    }

    @RequestMapping(method = PUT, value = "/applications/{id}")
    public ApplicationRepresentation put(@RequestBody final ApplicationRepresentation representation, @PathVariable("id") final String id) {
        representation.setId(id);
        store.put(id, merge(ApplicationRepresentation.class, findById(id), representation));
        return representation;
    }

    @RequestMapping(method = GET, value = "/applications/{id}/subscriptions")
    public ApplicationUserCollectionRepresentation subscriptions(@PathVariable("id") final String id) {
        final ApplicationUserCollectionRepresentation result = new ApplicationUserCollectionRepresentation();
        result.setUsers(Lists.newArrayList(new ApplicationUserRepresentation("tenant", "user", "password")));
        return result;
    }

    private <T> T merge(Class<T> clazz, Object oldValueObject, Object newValueObject) {
        final Map oldValue = objectMapper.convertValue(oldValueObject, Map.class);
        final Map newValue = objectMapper.convertValue(newValueObject, Map.class);
        oldValue.putAll(newValue);
        return objectMapper.convertValue(oldValue, clazz);
    }

    private ApplicationRepresentation findByName(String name) {
        for (final ApplicationRepresentation representation : store.values()) {
            if (name.equals(representation.getName())) {
                return representation;
            }
        }
        return null;
    }

    private ApplicationRepresentation findById(String id) {
        return store.get(id);
    }
}
