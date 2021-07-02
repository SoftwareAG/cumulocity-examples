package c8y.trackeragent.operations;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.UserCredentials;
import com.cumulocity.microservice.context.inject.UserScope;
import com.cumulocity.microservice.monitoring.health.indicator.platform.PlatformHealthIndicatorProperties;
import com.cumulocity.microservice.monitoring.health.indicator.subscription.SubscriptionHealthIndicatorProperties;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriberContext {

    @Bean(destroyMethod = "disconnect")
    @Autowired
    @UserScope
    public Subscriber<GId, OperationRepresentation> deviceControlNotificationsSubscriber(final ContextService<UserCredentials> contextService,
                                                                                         final DeviceControlApi deviceControlApi) throws SDKException {
        return new ContextScopedSubscriber<GId, OperationRepresentation>(deviceControlApi.getNotificationsSubscriber(), contextService);
    }

    @Bean
    @Autowired
    public SubscriptionHealthIndicatorProperties subscriptionHealthIndicatorProperties() {
        return new SubscriptionHealthIndicatorProperties();
    }

    @Bean
    @Autowired
    public PlatformHealthIndicatorProperties platformHealthIndicatorProperties() {
        return new PlatformHealthIndicatorProperties();
    }
}
