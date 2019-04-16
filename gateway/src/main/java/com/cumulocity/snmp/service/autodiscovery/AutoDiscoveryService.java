package com.cumulocity.snmp.service.autodiscovery;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.InventoryMediaType;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.gateway.GatewayFactory;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayAddedEvent;
import com.cumulocity.snmp.model.gateway.GatewayUpdateEvent;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.operation.OperationEvent;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.OperationRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.utils.IPAddressUtil;
import com.cumulocity.snmp.utils.gateway.Scheduler;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping.c8y_DeviceNotResponding;
import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping.c8y_DeviceSnmpNotEnabled;

@Slf4j
@Component
public class AutoDiscoveryService {

    private static int timeout=3000;

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

    @Autowired
    Scheduler scheduler;

    @Autowired
    private ManagedObjectRepository managedObjectRepository;

    @Autowired
    private Repository<Gateway> gatewayRepository;

    @Autowired
    private GatewayFactory gatewayFactory;

    ScheduledFuture<?> future = null;
    AtomicInteger currentSchedulingRateInSeconds = new AtomicInteger(0);
    AtomicBoolean isAutoDiscoveryInProgress = new AtomicBoolean();

    @EventListener
    public synchronized void update(final OperationEvent event) {
        final String ipRangeSet = event.getIpRange();
        final Gateway gateway = event.getGateway();

        if(!isAutoDiscoveryInProgress.get()) {
            try {
                isAutoDiscoveryInProgress.set(true);
                String[] ipRangeList = ipRangeSet.split(",");
                createRegisteredDeviceMap(gateway);
                operationRepository.executing(gateway, event.getOperationId());
                for (String ipRange : ipRangeList) {
                    startScanning(ipRange.split("-")[0], ipRange.split("-")[1], gateway);
                }
                isAutoDiscoveryInProgress.set(false);
                operationRepository.successful(gateway, event.getOperationId());
            } catch (Exception e){
                log.error(e.getMessage(),e);
                isAutoDiscoveryInProgress.set(false);
            }
        } else{
            operationRepository.failed(gateway, event.getOperationId(),"Device scanning via auto-discovery scheduler is in progress");
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void scheduleAutoDiscovery(final GatewayAddedEvent event) {
        if(event.getGateway().getAutoDiscoveryRateInMinutes()>0){
            refreshScheduler(event.getGateway());
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void scheduleAutoDiscovery(final GatewayUpdateEvent event) {
        if(event.getGateway().getAutoDiscoveryRateInMinutes()>0){
            refreshScheduler(event.getGateway());
        }
    }

    private void refreshScheduler(final Gateway gateway) {
        log.debug("Scheduling auto discovery on user defined interval if auto discovery scheduling rate exists");
        if (gateway.getIpRangeForAutoDiscovery() != null && (gateway.getAutoDiscoveryRateInMinutes() > 0)
                && (currentSchedulingRateInSeconds.get() != gateway.getAutoDiscoveryRateInMinutes())) {
            terminateTaskIfRunning();
            currentSchedulingRateInSeconds.set(gateway.getAutoDiscoveryRateInMinutes());
            future = scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    try {
                        if(!isAutoDiscoveryInProgress.get()) {
                            isAutoDiscoveryInProgress.set(true);
                            log.debug("Running scheduled auto discovery");
                            final Optional<ManagedObjectRepresentation> managedObject = managedObjectRepository.get(gateway);
                            if (managedObject.isPresent()) {
                                final Optional<Gateway> newGatewayOptional = gatewayFactory.create(gateway, managedObject.get());
                                if (newGatewayOptional.isPresent()) {
                                    final Gateway newGateway = newGatewayOptional.get();
                                    String[] ipRangeList = newGateway.getIpRangeForAutoDiscovery().split(",");
                                    createRegisteredDeviceMap(newGateway);
                                    for (String ipRange : ipRangeList) {
                                        startScanning(ipRange.split("-")[0], ipRange.split("-")[1], newGateway);
                                    }
                                }
                            }

//                            isAutoDiscoveryInProgress.set(false);
                        }
                    } catch (InvocationTargetException e) {
                        log.error("Exception during SNMP auto discovery device scan ",e);
                    } catch (IllegalAccessException e) {
                        log.error("Exception during SNMP auto discovery device scan ",e);
                    } finally {
                        isAutoDiscoveryInProgress.set(false);
                    }
                }
            }, 1000 * 60 * currentSchedulingRateInSeconds.get());
        }
    }

    private void terminateTaskIfRunning() {
        log.debug("Deleting auto discovery scheduling task if exists");
        if (future != null && (!future.isCancelled())) {
            future.cancel(true);
            isAutoDiscoveryInProgress.set(false);
        }
    }

    private void startScanning(String startIpAddress, String endIpAddress, Gateway gateway){
        if(IPAddressUtil.isValid(startIpAddress) && IPAddressUtil.isValid(endIpAddress)){
            IPAddressUtil startIp = new IPAddressUtil(startIpAddress);
            IPAddressUtil endIp = new IPAddressUtil(endIpAddress);

            do {
                try {
                    if (InetAddress.getByName(startIp.toString()).isReachable(timeout)){
                        boolean isSnmpEnabled = isDeviceSnmpEnabled(startIp.toString());
                        if(!mapIpAddressToGid.containsKey(startIp.toString()) && isSnmpEnabled) {
                            log.debug("A new device is found with IP Address " + startIp.toString() +", which is SNMP enabled.");
                            final Optional<ManagedObjectRepresentation> managedObjectOptional = inventoryRepository.save(gateway, managedObjectFactory.createChildDevice("Device-" + startIp.toString(),startIp.toString()));
                            if (managedObjectOptional.isPresent()) {
                                referenceChildDevice(gateway,managedObjectOptional.get().getId());
                            }
                         } else if(!isSnmpEnabled){
                            log.debug("A new device is found with IP Address " + startIp.toString() +", which is not SNMP enabled.");
                            eventPublisher.publishEvent(new UnknownTrapOrDeviceEvent(gateway, new ConfigEventType(
                                    "A new device is found with IP Address " + startIp.toString() +", which is not SNMP enabled."),c8y_DeviceSnmpNotEnabled+startIp.toString()));
                        }
                    } else{
                        if(mapIpAddressToGid.containsKey(startIp.toString())){
                            log.debug("No response from device with IP Address " + startIp.toString() +" during auto-discovery device scan.");
                            eventPublisher.publishEvent(new UnknownTrapOrDeviceEvent(gateway, new ConfigEventType(
                                    "No response from device with IP Address " + startIp.toString() +" during auto-discovery device scan."),c8y_DeviceNotResponding+startIp.toString()));
                        }
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }

                startIp = startIp.next();

            } while(!startIp.equals(endIp.next()));
        }
    }

    @RunWithinContext
    private ManagedObjectRepresentation createChildDevice(Gateway gateway,ManagedObjectRepresentation child) {
        return restOperations.post(buildPath(CHILD_DEVICES_PATH, gateway.getId().getValue()), InventoryMediaType.MANAGED_OBJECT,
                InventoryMediaType.MANAGED_OBJECT, child, ManagedObjectRepresentation.class);
    }

    @RunWithinContext
    private void referenceChildDevice(Gateway gateway, GId child) {
        ManagedObjectReferenceRepresentation childReference = new ManagedObjectReferenceRepresentation();
        ManagedObjectRepresentation childMO = new ManagedObjectRepresentation();
        childMO.setId(child);
        childReference.setManagedObject(childMO);
        restOperations.post(buildPath(CHILD_DEVICES_PATH, gateway.getId().getValue()), InventoryMediaType.MANAGED_OBJECT_REFERENCE, childReference);
    }

    @SuppressWarnings("SameParameterValue")
    private String buildPath(String raw, String onlyOnePathVariable) {
        UriBuilder builder = UriBuilder.fromPath(raw);
        return builder.build(onlyOnePathVariable).getPath();
    }

    private boolean isDeviceSnmpEnabled(String ipAddress){
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);

        TransportMapping transport = null;
        try {
            transport = new DefaultUdpTransportMapping();
            transport.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snmp snmp = new Snmp(transport);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setVersion(SnmpConstants.version1);

        target.setAddress(new UdpAddress(ipAddress + "/" +161));

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

    @RunWithinContext
    private void createRegisteredDeviceMap(Gateway gateway){
        final List<GId> currentDeviceIds = gateway.getCurrentDeviceIds();
        mapIpAddressToGid.clear();
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
