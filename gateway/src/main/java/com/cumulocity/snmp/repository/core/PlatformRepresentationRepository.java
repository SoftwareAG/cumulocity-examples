package com.cumulocity.snmp.repository.core;

import com.cumulocity.snmp.model.core.Credentials;
import com.google.common.base.Optional;
import lombok.NonNull;

public interface PlatformRepresentationRepository<E> {
    Optional<E> apply(@NonNull Credentials gateway, @NonNull E value);
}
