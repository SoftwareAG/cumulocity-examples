package com.cumulocity.tixi.server.components;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.cumulocity.agent.server.context.DeviceCredentailsResolver;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.model.idtype.GId;
import com.google.common.base.Optional;

@Named
public class RequestParametersDeviceCredentialsResolver implements DeviceCredentailsResolver<HttpServletRequest> {

    @Override
    public Optional<DeviceCredentials> get(HttpServletRequest input) {
        final Optional<String> username = Optional.fromNullable(input.getParameter("user"));
        final Optional<String> password = Optional.fromNullable(input.getParameter("password"));
        final Optional<String> deviceId = Optional.fromNullable(input.getParameter("deviceID"));
        final Optional<String> serial = Optional.fromNullable(input.getParameter("serial"));
        if (!username.isPresent()) {
            return Optional.<DeviceCredentials> absent();
        }
        final String[] splited = DeviceCredentials.splitUsername(username.get());
        return Optional.of(new DeviceCredentials(splited[0], splited[1], password.get(), null, GId.asGId(deviceId.orNull())));
    }

}
