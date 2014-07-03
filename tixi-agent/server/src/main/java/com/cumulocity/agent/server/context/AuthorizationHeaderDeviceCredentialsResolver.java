package com.cumulocity.agent.server.context;

import static com.cumulocity.sdk.client.PagedCollectionResource.PAGE_SIZE_KEY;
import static com.cumulocity.sdk.client.RestConnector.X_CUMULOCITY_APPLICATION_KEY;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Integer.parseInt;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public class AuthorizationHeaderDeviceCredentialsResolver implements DeviceCredentailsResolver<HttpServletRequest> {

    private static final Integer DEFAULT_PAGE_SIZE = 5;

    private static final Function<String, Integer> toInt = new Function<String, Integer>() {
        @Override
        public Integer apply(String s) {
            return parseInt(s);
        }
    };

    @Override
    public Optional<DeviceCredentials> get(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        String applicationKey = request.getHeader(X_CUMULOCITY_APPLICATION_KEY);
        int pageSize = fromNullable(request.getParameter(PAGE_SIZE_KEY)).transform(toInt).or(DEFAULT_PAGE_SIZE);
        return isNullOrEmpty(authorization) ? Optional.<DeviceCredentials> absent() : Optional.of(DeviceCredentials.from(authorization,
                applicationKey, pageSize));
    }

}
