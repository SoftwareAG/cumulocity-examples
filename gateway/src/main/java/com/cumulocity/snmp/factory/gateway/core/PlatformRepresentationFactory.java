package com.cumulocity.snmp.factory.gateway.core;

import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.google.common.base.Optional;

public interface PlatformRepresentationFactory<M extends Mapping, R> {
    Optional<R> apply(PlatformRepresentationEvent platformRepresentationEvent);
}