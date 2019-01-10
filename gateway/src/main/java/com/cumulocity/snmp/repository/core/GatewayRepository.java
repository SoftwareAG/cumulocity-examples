package com.cumulocity.snmp.repository.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.google.common.base.Optional;
import lombok.NonNull;

import java.util.Collection;

public interface GatewayRepository<E> {
    @NonNull
    Optional<E> get(@NonNull Gateway gateway, @NonNull GId key);

    @NonNull
    Collection<E> findAll(@NonNull Gateway gateway);

    boolean exists(@NonNull Gateway gateway, @NonNull GId value);

    E save(@NonNull Gateway gateway, @NonNull E value);

    E delete(@NonNull Gateway gateway, @NonNull GId value);
}
