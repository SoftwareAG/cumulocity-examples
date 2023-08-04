package c8y.example.notification.samples;

import c8y.example.notification.client.platform.SubscriptionRepository;
import c8y.example.notification.client.platform.TokenService;
import com.cumulocity.model.authentication.CumulocityCredentialsFactory;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformBuilder;
import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

@Getter
public enum PlatformConfiguration {

    INSTANCE;

    private static final String CUMULOCITY_BASE_URL = "c8y.baseURL";
    private static final String CUMULOCITY_TENANT = "c8y.tenant";
    private static final String CUMULOCITY_USERNAME = "c8y.username";
    private static final String CUMULOCITY_PASSWORD = "c8y.password";

    private static final String CUMULOCITY_SOURCE_ID = "c8y.source.id";
    private static final String CUMULOCITY_NOTIFICATION2_WS_URL = "c8y.notification2.websocket.url";
    private static final String CUMULOCITY_NOTIFICATION2_SUBSCRIBER = "c8y.notification2.subscriber";

    private final String websocketUrl;
    private final Platform platform;
    private final TokenService tokenService;
    private final SubscriptionRepository subscriptionRepository;

    PlatformConfiguration() {
        try {
            final Configuration configuration = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                    PropertiesConfiguration.class)
                    .configure(new Parameters()
                            .properties()
                            .setFileName("notifications-example.properties"))
                    .getConfiguration();

            this.platform = PlatformBuilder.platform()
                    .withBaseUrl(configuration.getString(CUMULOCITY_BASE_URL))
                    .withCredentials(new CumulocityCredentialsFactory()
                            .withTenant(configuration.getString(CUMULOCITY_TENANT))
                            .withUsername(configuration.getString(CUMULOCITY_USERNAME))
                            .withPassword(configuration.getString(CUMULOCITY_PASSWORD))
                            .getCredentials())
                    .build();

            this.websocketUrl = configuration.getString(CUMULOCITY_NOTIFICATION2_WS_URL);

            this.tokenService = new TokenService(this.platform.getTokenApi());
            this.subscriptionRepository = new SubscriptionRepository(this.platform.getNotificationSubscriptionApi());
        } catch (ConfigurationException e) {
            throw new RuntimeException("Exception occurred when loading properties", e);
        }
    }
}
