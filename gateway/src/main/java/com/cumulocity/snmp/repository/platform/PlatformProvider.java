package com.cumulocity.snmp.repository.platform;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.authentication.CumulocityCredentialsFactory;
import com.cumulocity.sdk.client.ClientConfiguration;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.repository.platform.PlatformProperties.DeviceBootstrapConfigurationProperties;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.HOURS;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformProvider {

    @lombok.Value
    @Builder
    private static class Key {
        @NonNull
        private final String tenant;
        @NonNull
        private final String user;
        @NonNull
        private final String password;
    }

    private final PlatformProperties platformProperties;

    private final LoadingCache<Key, PlatformImpl> platforms = newBuilder().expireAfterAccess(1, HOURS)
            .removalListener(new RemovalListener<Key, PlatformImpl>() {
                @Override
                public void onRemoval(RemovalNotification<Key, PlatformImpl> removalNotification) {
                    final PlatformImpl platform = removalNotification.getValue();
                    platform.createRestConnector().getClient().destroy();
                    platform.close();
                }
            })
            .build(new CacheLoader<Key, PlatformImpl>() {
                public PlatformImpl load(final Key key) {
                    final String url = platformProperties.getUrl();
                    final CumulocityCredentials cred = initCredentials(key.getUser(), key.getPassword(), key.getTenant());
                    final ClientConfiguration conf = new ClientConfiguration(null, false);
                    final PlatformImpl result = new PlatformImpl(url, cred, conf);

                    result.setForceInitialHost(platformProperties.isForceInitialHost());
                    return result;
                }

                @Nonnull
                private CumulocityCredentials initCredentials(final String user, String password, String tenant) {
                    return new CumulocityCredentialsFactory()
                            .withUsername(user)
                            .withPassword(password)
                            .withTenant(tenant)
                            .getCredentials();
                }
            });

    public Platform getBootstrapPlatform() throws ExecutionException {
        final DeviceBootstrapConfigurationProperties bootstrap = platformProperties.getBootstrap();
        return getPlatform(bootstrap.getUser(), bootstrap.getPassword(), bootstrap.getTenant());
    }

    @Nonnull
    public Platform getPlatform(final Credentials gateway) throws ExecutionException {
        final String username = gateway.getName();
        final String password = gateway.getPassword();
        final String tenant = gateway.getTenant();
        return getPlatform(username, password, tenant);
    }

    @Nonnull
    private Platform getPlatform(final String user, final String password, final String tenant) throws ExecutionException {
        return platforms.get(Key.builder().user(user).password(password).tenant(tenant).build());
    }
}
