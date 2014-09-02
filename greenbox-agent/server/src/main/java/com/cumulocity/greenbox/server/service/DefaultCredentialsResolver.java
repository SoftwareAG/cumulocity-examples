package com.cumulocity.greenbox.server.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;

import com.cumulocity.agent.server.context.DeviceCredentailsResolver;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.google.common.base.Optional;

public class DefaultCredentialsResolver implements DeviceCredentailsResolver<HttpServletRequest> {

    @Value("${C8Y.tenant}")
    private String tenant;

    @Value("${C8Y.username}")
    private String username;

    @Value("${C8Y.password}")
    private String password;

    @Override
    public Optional<DeviceCredentials> get(HttpServletRequest input) {
        return Optional.of(new DeviceCredentials(tenant, username, password, null, null));
    }

}
