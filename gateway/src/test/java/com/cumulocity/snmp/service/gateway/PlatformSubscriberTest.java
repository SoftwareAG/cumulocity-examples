package com.cumulocity.snmp.service.gateway;

import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.cep.notification.InventoryRealtimeDeleteAwareNotificationsSubscriber;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayAddedEvent;
import com.cumulocity.snmp.model.notification.Notifications;
import com.cumulocity.snmp.model.notification.platform.ManagedObjectListener;
import com.cumulocity.snmp.platform.PlatformSubscribedEvent;
import com.cumulocity.snmp.repository.platform.PlatformProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.ExecutionException;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PlatformSubscriberTest {

    @Mock
    PlatformProvider platformProvider;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    InventoryRealtimeDeleteAwareNotificationsSubscriber inventorySubscriber;

    @Mock
    Notifications notifications;

    @Mock
    PlatformParameters platformParameters;

    @InjectMocks
    PlatformSubscriber platformSubscriber;

    @Before
    public void before() throws ExecutionException {

        MockitoAnnotations.initMocks(this);
        when(platformProvider.getPlatformProperties(any(Gateway.class))).thenReturn(platformParameters);
    }

    @Test
    public void shouldTestGatewayAddedEvent(){

        //Given
        Gateway gateway = createGateway();
        when(notifications.subscribeInventory(eq(platformParameters),eq(gateway.getId()),any(ManagedObjectListener.class))).thenReturn(inventorySubscriber);

        ///When
        platformSubscriber.refreshSubscriptions(new GatewayAddedEvent(gateway));

        //Then
        verify(eventPublisher).publishEvent(any(PlatformSubscribedEvent.class));
    }

    private Gateway createGateway() {
        return new Gateway().withId(asGId(11225));
    }
}