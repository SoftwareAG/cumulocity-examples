package com.cumulocity.snmp.service.autodiscovery;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.InventoryMediaType;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapRecievedEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.operation.Operation;
import com.cumulocity.snmp.model.operation.OperationEvent;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.OperationRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.utils.IPAddressUtil;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AutoDiscoveryService {

    private static int timeout=1000;

    private static final String CHILD_DEVICES_PATH = "/inventory/managedObjects/{deviceId}/childDevices";

    Map<String, GId> mapIpAddressToGid = new HashMap<>();

    @Autowired
    ManagedObjectRepository inventoryRepository;

    @Autowired
    ManagedObjectFactory managedObjectFactory;

    @Autowired
    private RestOperations restOperations;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    Repository<Device> deviceRepository;

    @Autowired
    private OperationRepository operationRepository;

    @EventListener
    @RunWithinContext
    public synchronized void update(final OperationEvent event) {
        final Operation operation = event.getOperation();
        final Gateway gateway = event.getGateway();

        try {
            //TODO: Segregate the IP range received and scan for each range
           startScanning("","", gateway);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            operationRepository.failed(gateway, operation.getId(), ex.getMessage());
        }
    }

    private void startScanning(String startIpAddress, String endIpAddress, Gateway gateway){
        if(IPAddressUtil.isValid(startIpAddress) && IPAddressUtil.isValid(endIpAddress)){
            IPAddressUtil startIp = new IPAddressUtil(startIpAddress);
            IPAddressUtil endIp = new IPAddressUtil(endIpAddress);

            do {
                startIp = startIp.next();
                createRegisteredDeviceMap(gateway);
                try {
                    if (InetAddress.getByName(startIp.toString()).isReachable(timeout)){
                        boolean isSnmpEnabled = isDeviceSnmpEnabled(startIp.toString());
                        if(!mapIpAddressToGid.containsKey(startIp.toString()) && isSnmpEnabled) {
                            final Optional<ManagedObjectRepresentation> managedObjectOptional = inventoryRepository.save(gateway, managedObjectFactory.create("Device-" + startIp.toString()));
                            if (managedObjectOptional.isPresent()) {
                                createChildDevice(managedObjectOptional.get(), gateway.getId());
                            }
                        } else if(!isSnmpEnabled){
                            //TODO: Change the publishing event class name and fragment type.
                            eventPublisher.publishEvent(new UnknownTrapRecievedEvent(gateway, new ConfigEventType(
                                    "A new device is found with IP Address " + startIp.toString() +", which is not SNMP enabled.")));
                        }
                    } else{
                        //Checking if the device(IP Address) is registered under the Gateway and raise Alarm/Event
                        if(mapIpAddressToGid.containsKey(startIp.toString())){
                            //TODO: Change the publishing event class name and fragment type.
                            eventPublisher.publishEvent(new UnknownTrapRecievedEvent(gateway, new ConfigEventType(
                                    "No response from device with IP Address " + startIp.toString() +" during auto-discovery device scan.")));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } while(!startIp.equals(endIp));
        }
    }

    private ManagedObjectRepresentation createChildDevice(ManagedObjectRepresentation child, GId parentId) {
        return restOperations.post(buildPath(CHILD_DEVICES_PATH, parentId.getValue()), InventoryMediaType.MANAGED_OBJECT,
                InventoryMediaType.MANAGED_OBJECT, child, ManagedObjectRepresentation.class);
    }

    private void referenceChildDevice(GId parent, GId child) {
        ManagedObjectReferenceRepresentation childReference = new ManagedObjectReferenceRepresentation();
        ManagedObjectRepresentation childMO = new ManagedObjectRepresentation();
        childMO.setId(child);
        childReference.setManagedObject(childMO);
        restOperations.post(buildPath(CHILD_DEVICES_PATH, parent.getValue()), InventoryMediaType.MANAGED_OBJECT_REFERENCE, childReference);
    }

    @SuppressWarnings("SameParameterValue")
    private String buildPath(String raw, String onlyOnePathVariable) {
        UriBuilder builder = UriBuilder.fromPath(raw);
        return builder.build(onlyOnePathVariable).getPath();
    }

    private boolean isDeviceSnmpEnabled(String ipAddress){
        PDU pdu = new PDU();
        /*pdu.add(new VariableBinding(new OID(new int[] {1,3,6,1,2,1,1,1})));
        pdu.add(new VariableBinding(new OID(new int[] {1,3,6,1,2,1,1,2})));*/
        pdu.setType(PDU.GET);

        TransportMapping transport = null;
        try {
            transport = new DefaultTcpTransportMapping();
            transport.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snmp snmp = new Snmp(transport);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setVersion(SnmpConstants.version2c);

        target.setAddress(new TcpAddress(ipAddress + "/" +161));
        /*target.setRetries(1);
        target.setTimeout(5000);*/

        try {
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if (response!= null) {
                return true;
            }
        } catch (IOException e) {
            log.error("Exception while processing SNMP compatibility check", e);
        }

        return false;
    }

    private void createRegisteredDeviceMap(Gateway gateway){
        final List<GId> currentDeviceIds = gateway.getCurrentDeviceIds();
        if (currentDeviceIds != null) {
            for (final GId gId : currentDeviceIds) {
                final Optional<Device> deviceOptional = deviceRepository.get(gId);
                if (deviceOptional.isPresent()) {
                    final Device device = deviceOptional.get();
                    mapIpAddressToGid.put(device.getIpAddress(),device.getId());
                }
            }
        }
    }
}
