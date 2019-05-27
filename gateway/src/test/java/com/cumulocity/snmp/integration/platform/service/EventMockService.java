package com.cumulocity.snmp.integration.platform.service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventMockService {
    public static final String EVENT_PATH = "/event/events";

    private final PlatformProperties platformProperties;

    private List store = new ArrayList<>();

    public void save(EventRepresentation event) {
        final int id = RandomUtils.nextInt(1, 10000);
        event.setId(GId.asGId(id));
        event.setSelf(platformProperties.getUrl() + EVENT_PATH + "/" + event.getId().getValue());

        this.store.add(event);
    }
}
