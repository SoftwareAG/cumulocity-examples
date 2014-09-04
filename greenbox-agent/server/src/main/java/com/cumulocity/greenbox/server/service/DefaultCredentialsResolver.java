package com.cumulocity.greenbox.server.service;

import javax.ws.rs.container.ContainerRequestContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceCredentailsResolver;
import com.cumulocity.agent.server.context.DeviceCredentials;

@Component
public class DefaultCredentialsResolver implements DeviceCredentailsResolver<ContainerRequestContext> {

    @Value("${C8Y.tenant}")
    private String tenant;

    @Value("${C8Y.username}")
    private String username;

    @Value("${C8Y.password}")
    private String password;

    @Override
    public DeviceCredentials get(ContainerRequestContext input) {
        return new DeviceCredentials(tenant, username, password, null, null);
    }

    @Override
    public boolean supports(Object credentialSource) {
        return true;
    }

}
