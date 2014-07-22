package com.cumulocity.agent.server.context;

import static com.google.common.cache.CacheBuilder.newBuilder;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CumulocityClientCache extends CacheLoader<DeviceCredentials, PlatformImpl> {

    private final String host;

    private final Optional<String> proxy;

    private final Optional<Integer> proxyPort;

    private final LoadingCache<DeviceCredentials, PlatformImpl> cache;

    public CumulocityClientCache(String host, String proxy, Integer proxyPort) {
        this.host = host;
        this.proxy = Optional.fromNullable(proxy);
        this.proxyPort = Optional.fromNullable(proxyPort);
        this.cache = newBuilder().build(this);
    }

    public PlatformImpl get(DeviceCredentials login) throws Exception {
        return cache.get(login);
    }

    @Override
    public PlatformImpl load(DeviceCredentials login) throws Exception {

        return proxy(new PlatformImpl(host, new CumulocityCredentials(login.getTenant(), login.getUsername(), login.getPassword(),
                login.getAppKey()), login.getPageSize()));
    }

    private PlatformImpl proxy(PlatformImpl platform) {
        String proxyHost = proxy.or("");
        if (proxyHost.length() > 0) {
            platform.setProxyHost(proxyHost);
        }
        Integer port = proxyPort.or(0);
        if (port > 0) {
            platform.setProxyPort(port);
        }
        return platform;
    }

    @Override
    public String toString() {
        return "CumulocityClientCache{" + "host='" + host + '\'' + ", proxy=" + proxy + ", proxyPort=" + proxyPort + "}";
    }
}
