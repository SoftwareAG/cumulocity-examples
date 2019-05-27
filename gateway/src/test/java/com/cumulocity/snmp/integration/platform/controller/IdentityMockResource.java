package com.cumulocity.snmp.integration.platform.controller;

import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Lazy
@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IdentityMockResource {

    public static final String EXTERNAL_ID_PATH = "/externalIds/{type}/{externaId}";
    public static final String EXTERNAL_ID_OF_GLOBAL_PATH = "/globalIds/{globalId}/externalIds";

    private final PlatformProperties platformProperties;

    @RequestMapping(method = POST, value = EXTERNAL_ID_OF_GLOBAL_PATH)
    public ResponseEntity<ExternalIDRepresentation> storeExternalId(@PathVariable("globalId") String globalId) throws URISyntaxException {
        final ExternalIDRepresentation representation = new ExternalIDRepresentation();
        representation.setSelf(platformProperties.getUrl() + "/globalIds/" + globalId + "/externalIds");
        return ResponseEntity.created(new URI(representation.getSelf())).body(representation);
    }

    @RequestMapping(method = GET, value = EXTERNAL_ID_PATH)
    public ResponseEntity<?> getExternalId() throws URISyntaxException {
        return ResponseEntity.notFound().build();
    }

}
