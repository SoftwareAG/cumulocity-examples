package com.cumulocity.snmp.service.autodiscovery;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayUpdateEvent;
import com.cumulocity.snmp.model.operation.OperationEvent;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.OperationRepository;
import com.cumulocity.snmp.utils.gateway.Scheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AutoDiscoveryServiceTest {

    @Mock
    private ManagedObjectRepository inventoryRepository;

    @Mock
    com.google.common.base.Optional<ManagedObjectRepresentation> optional;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private SNMPConfigurationProperties config;

    @Mock
    Scheduler scheduler;

    @InjectMocks
    private AutoDiscoveryService autoDiscoveryService;


    @Test
    public void shouldTestAutoDiscovery(){
        OperationEvent operationEvent = new OperationEvent(createGateway(), asGId(11226));
        Gateway gateway = createGateway();
        //Given
        when(inventoryRepository.get(gateway,gateway.getId())).thenReturn(optional);
        //When
        autoDiscoveryService.update(operationEvent);
        //Then
        verify(operationRepository).executing(gateway,asGId(11226));
        verify(operationRepository).successful(gateway,asGId(11226));
    }

    @Test
    public void shouldTestAutoDiscoveryWithScheduler(){
        //When
        autoDiscoveryService.scheduleAutoDiscovery(new GatewayUpdateEvent(createGateway()));
        //Then
        verify(scheduler).scheduleWithFixedDelay(any(Runnable.class),eq(60000L));
    }

    private Gateway createGateway() {
        return new Gateway().withId(asGId(11225)).withAutoDiscoveryRateInMinutes(1).withIpRangeForAutoDiscovery("192.168.0.1-192.168.0.5");
    }
}