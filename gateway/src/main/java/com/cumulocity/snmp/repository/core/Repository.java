package com.cumulocity.snmp.repository.core;

import com.cumulocity.model.idtype.GId;
import com.google.common.base.Optional;
import lombok.NonNull;

import java.util.Collection;

public interface Repository<E> {
    @NonNull
    Optional<E> get(@NonNull GId key);

    @NonNull
    Collection<E> findAll();

    boolean exists(@NonNull GId value);

    E save(@NonNull E value);

    E delete(@NonNull GId value);

    void clear();
}
