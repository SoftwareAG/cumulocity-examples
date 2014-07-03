package com.cumulocity.agent.server.context;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Optional;

public class ContextFilter extends OncePerRequestFilter {

    private final DeviceContextService contextService;

    private final DeviceBootstrapDeviceCredentialsSupplier deviceBootstrapDeviceCredentialsSupplier;

    private final List<DeviceCredentailsResolver<HttpServletRequest>> deviceCredentailsResolvers;

    @Autowired
    public ContextFilter(DeviceContextService contextService,
            List<DeviceCredentailsResolver<HttpServletRequest>> deviceCredentailsResolvers,
            DeviceBootstrapDeviceCredentialsSupplier deviceBootstrapDeviceCredentialsSupplier) {
        this.contextService = contextService;
        this.deviceCredentailsResolvers = deviceCredentailsResolvers;
        this.deviceBootstrapDeviceCredentialsSupplier = deviceBootstrapDeviceCredentialsSupplier;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        DeviceContext context = new DeviceContext(getCredentials(request));
        try {
            contextService.callWithinContext(context, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    filterChain.doFilter(request, response);
                    return null;
                }
            });
        } catch (Exception e) {
            throw new ServletException("Unexpected exception!", e);
        }
    }

    private DeviceCredentials getCredentials(final HttpServletRequest request) {
        Optional<DeviceCredentials> deviceCredetials = Optional.absent();

        for (DeviceCredentailsResolver<HttpServletRequest> resolver : deviceCredentailsResolvers) {
            deviceCredetials = deviceCredetials.or(resolver.get(request));
        }
        return deviceCredetials.or(deviceBootstrapDeviceCredentialsSupplier);

    }
}
