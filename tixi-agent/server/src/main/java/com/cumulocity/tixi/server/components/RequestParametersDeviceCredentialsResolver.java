package com.cumulocity.tixi.server.components;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceCredentailsResolver;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;
import com.google.common.base.Optional;

@Component
public class RequestParametersDeviceCredentialsResolver implements DeviceCredentailsResolver<HttpServletRequest> {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestParametersDeviceCredentialsResolver.class);

    @Override
    public Optional<DeviceCredentials> get(HttpServletRequest input) {
        final Optional<String> username = Optional.fromNullable(input.getParameter("user"));
        final Optional<String> password = Optional.fromNullable(input.getParameter("password"));
        final Optional<String> deviceId = Optional.fromNullable(input.getParameter("deviceID"));
        if (!username.isPresent()) {
        	logger.debug("there is no username in request parameter!");
            return Optional.<DeviceCredentials> absent();
        }
        final String[] splited = username.get().split(TixiDeviceCredentails.USERNAME_SEPARATOR);
        DeviceCredentials deviceCredentials = new DeviceCredentials(splited[0], splited[1], password.get(), null, GId.asGId(deviceId.orNull()));
        logger.debug("Device credentials created: {}", deviceCredentials);
		return Optional.of(deviceCredentials);
    }

}
