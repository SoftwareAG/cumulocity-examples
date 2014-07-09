package com.cumulocity.agent.server.context;

import java.io.UnsupportedEncodingException;

import org.glassfish.jersey.internal.util.Base64;

import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;

public class DeviceCredentials {

    public static final int DEFAULT_PAGE_SIZE = 25;

    public static final String AUTH_ENCODING = "ASCII";

    public static final String AUTH_PREFIX = "BASIC ";

    public static final String AUTH_SEPARATOR = ":";

    public static final String LOGIN_SEPARATOR = "/";

    private final String tenant;

    private final String username;

    private final String password;

    private final String appKey;

    private final ID deviceId;

    private final int pageSize;

    public DeviceCredentials(String tenant, String username, String password, String appKey, ID deviceId) {
        this(tenant, username, password, appKey, deviceId, DEFAULT_PAGE_SIZE);
    }

    public DeviceCredentials(String tenant, String username, String password, String appKey, ID deviceId, int pageSize) {
        this.tenant = tenant;
        this.username = username;
        this.password = password;
        this.appKey = appKey;
        this.deviceId = deviceId;
        this.pageSize = pageSize;
    }

    public static DeviceCredentials from(DeviceCredentialsRepresentation credentials) {
        return new DeviceCredentials(credentials.getTenantId(), credentials.getUsername(), credentials.getPassword(), null, null);
    }

    public static DeviceCredentials from(String authorization, String appKey, int pageSize) {
        if (authorization == null || !authorization.toUpperCase().startsWith(AUTH_PREFIX)) {
            return new DeviceCredentials(null, "", "", appKey, null, pageSize);
        }

        String[] loginAndPass = decode(authorization);
        String tenant = null;
        String username = loginAndPass.length > 0 ? loginAndPass[0] : "";
        String password = loginAndPass.length > 1 ? loginAndPass[1] : "";

        String[] tenantAndUsername = splitUsername(username);
        if (tenantAndUsername.length > 1) {
            tenant = tenantAndUsername[0];
            username = tenantAndUsername[1];
        }

        return new DeviceCredentials(tenant, username, password, appKey, null, pageSize);
    }

    public static String[] splitUsername(String username) {
        return username.split(LOGIN_SEPARATOR);
    }

    public static String[] decode(String authorization) {
        try {
            return new String(Base64.decode(authorization.substring(AUTH_PREFIX.length()).getBytes()), AUTH_ENCODING).split(AUTH_SEPARATOR, 2);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(String tenant, String username, String password) {
        return encode(tenant + LOGIN_SEPARATOR + username, password);
    }

    public static String encode(String login, String password) {
        try {
            return AUTH_PREFIX + new String(Base64.encode((login + AUTH_SEPARATOR + password).getBytes()), AUTH_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTenant() {
        return tenant;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAppKey() {
        return appKey;
    }

    public ID getDeviceId() {
        return deviceId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getAuthorization() {
        if (tenant == null) {
            return encode(username, password);
        } else {
            return encode(tenant, username, password);
        }
    }

    @Override
    public String toString() {
        return tenant;
    }

}
