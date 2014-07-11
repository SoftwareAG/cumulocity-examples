package com.cumulocity.agent.server.context;

import com.google.common.base.Optional;

public interface DeviceCredentailsResolver<T> {

    Optional<DeviceCredentials> get(T input);

}
