package com.cumulocity.agent.snmp.bootstrap.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class DeviceCredentialsKey implements Serializable {
    private final String bootstrapUrl;

    private final String bootstrapTenant;

    private final String bootstrapUser;
}
