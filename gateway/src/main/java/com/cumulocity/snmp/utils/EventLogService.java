package com.cumulocity.snmp.utils;

import com.cumulocity.snmp.model.core.Credentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventLogService {

    public static final int MAX_LENGTH = 900;

    private final ObjectMapper objectMapper;

    @Getter(lazy = true)
    private final ObjectMapper loggerObjectMapper = createLoggerObjectMapper();

    @Order(value = HIGHEST_PRECEDENCE)
    @EventListener
    public void onEvent(Object event) {
        try {
            if (log.isDebugEnabled()) {
                if (event.getClass().getName().contains("cumulocity")) {
                    String string = getLoggerObjectMapper().writeValueAsString(event);
                    if (string.length() > MAX_LENGTH) {
                        string = string.substring(0, MAX_LENGTH);
                    }
                    log.debug(event.getClass().getSimpleName() + "(" + string + ")");
                }
            }
        } catch (final Exception ex) {
            log.error(ex.getMessage());
            try {
                log.debug(event.getClass().getSimpleName() + "(" + event + ")");
            } catch (final Exception exx) {
                log.error(ex.getMessage());
            }
        }
    }

    private ObjectMapper createLoggerObjectMapper() {
        final ObjectMapper result = objectMapper.copy();
        result.addMixIn(Credentials.class, CredentialsMixin.class);
        return result;
    }

    abstract class CredentialsMixin {
        @JsonIgnore
        abstract public String getPassword();
    }
}