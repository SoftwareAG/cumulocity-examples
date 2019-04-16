package com.cumulocity.snmp.service.autodiscovery;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.operation.Operation;
import com.cumulocity.snmp.model.operation.OperationEvent;
import com.cumulocity.snmp.repository.core.Repository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.junit.Assert.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class AutoDiscoveryServiceTest {

    @Autowired
    Repository<Device> deviceRepository;

    @InjectMocks
    AutoDiscoveryService autoDiscoveryService;

    @Before
    public void setUp(){
        deviceRepository.save(new Device().withId(asGId(11223)).withIpAddress("192.168.0.1"));
        deviceRepository.save(new Device().withId(asGId(11224)).withIpAddress("192.168.0.2"));
    }
    @Test
    public void shouldTestAutoDisocvery(){
        Operation operation = new Operation();
//        operation.add("192.168.0.1-192.168.0.5,192.168.1.1-192.168.1.5");
        OperationEvent operationEvent = new OperationEvent(createGateway(),"", asGId(11226));

        autoDiscoveryService.update(operationEvent);
    }

    private Gateway createGateway() {
        List<GId> gIdList = new ArrayList();
        gIdList.add(asGId(11223));
        gIdList.add(asGId(11224));
        return new Gateway().withId(asGId(11225)).withCurrentDeviceIds(gIdList);
    }
}