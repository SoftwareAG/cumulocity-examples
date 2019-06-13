package com.cumulocity.snmp.service.autodiscovery;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.InventoryMediaType;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.factory.gateway.DeviceFactory;
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
import com.googlecode.ipv6.IPv6Address;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

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
@Service
public class AutoDiscoveryService {

    private static final String CHILD_DEVICES_PATH = "/inventory/managedObjects/{deviceId}/childDevices";

    private ScheduledFuture<?> future = null;
    private AtomicInteger currentSchedulingRateInSeconds = new AtomicInteger(0);
    private AtomicBoolean isAutoDiscoveryInProgress = new AtomicBoolean();
    private Map<String, GId> mapIpAddressToGid = new HashMap<>();

    @Autowired
    private SNMPConfigurationProperties config;

    @Autowired
    private ManagedObjectRepository inventoryRepository;

    @Autowired
    private ManagedObjectFactory managedObjectFactory;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private RestOperations restOperations;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private Repository<Gateway> gatewayRepository;

    @Autowired
    private GatewayFactory gatewayFactory;

    @Autowired
    private DeviceFactory deviceFactory;

    private static final String UDP = "udp";

    private static final String TCP = "tcp";

    @EventListener
    @Synchronized
    public void update(final OperationEvent event) {
        final Gateway gateway = event.getGateway();
        if (!isAutoDiscoveryInProgress.get()) {
            try {
                isAutoDiscoveryInProgress.set(true);
                String[] ipRangeList = gateway.getIpRangeForAutoDiscovery().split(",");
                createRegisteredDeviceMap(gateway);
                operationRepository.executing(gateway, event.getOperationId());
                for (String ipRange : ipRangeList) {
                    filterIpForDeviceScan(ipRange.split("-")[0], ipRange.split("-")[1], gateway);
                }
                operationRepository.successful(gateway, event.getOperationId());
            } catch (NullPointerException e) {
                log.error("The ip range is not saved to managed object. Please save the ip range to managed object /inventory/managedObjects");
                log.error(e.getMessage(), e);
            } catch (ArrayIndexOutOfBoundsException e){
                log.error("Please make sure the ip range format is correct. E.g The correct format should be '10.23.52.51-10.23.52.54' or 'fe80::aad:996f:3d24:2911-fe80::aad:996f:3d24:2918'");
                operationRepository.failed(gateway, event.getOperationId(), "Auto-discovery process failed as the ip range format is wrong.");
                log.error(e.getMessage(), e);
            } catch (Exception e){
                log.error(e.getMessage(), e);
            } finally {
                isAutoDiscoveryInProgress.set(false);
            }

        } else {
            operationRepository.failed(gateway, event.getOperationId(), "Device scanning via auto-discovery scheduler is already in progress");
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void scheduleAutoDiscovery(final GatewayAddedEvent event) {
        if (event.getGateway() != null) {
            scheduleAutoDiscovery(event.getGateway());
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void scheduleAutoDiscovery(final GatewayUpdateEvent event) {
        if (event.getGateway() != null) {
            scheduleAutoDiscovery(event.getGateway());
        }
    }

    private void scheduleAutoDiscovery(Gateway gateway) {
        if (gateway.getAutoDiscoveryRateInMinutes() > 0) {
            refreshScheduler(gateway);
        } else {
            terminateTaskIfRunning();
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
                        if (!isAutoDiscoveryInProgress.get()) {
                            isAutoDiscoveryInProgress.set(true);
                            log.debug("Running scheduled auto discovery with a delay of " + gateway.getAutoDiscoveryRateInMinutes() + " minute(s)");
                            final Optional<ManagedObjectRepresentation> managedObject = inventoryRepository.get(gateway);
                            if (managedObject.isPresent()) {
                                final Optional<Gateway> newGatewayOptional = gatewayFactory.create(gateway, managedObject.get());
                                if (newGatewayOptional.isPresent()) {
                                    final Gateway newGateway = newGatewayOptional.get();
                                    String[] ipRangeList = newGateway.getIpRangeForAutoDiscovery().split(",");
                                    createRegisteredDeviceMap(newGateway);
                                    for (String ipRange : ipRangeList) {
                                        filterIpForDeviceScan(ipRange.split("-")[0], ipRange.split("-")[1], newGateway);
                                    }
                                }
                            }
                        }
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        log.error("Exception during SNMP auto discovery device scan ", e);
                    } finally {
                        isAutoDiscoveryInProgress.set(false);
                    }
                }
            }, 1000 * 60 * currentSchedulingRateInSeconds.get());
        }
    }

    private void terminateTaskIfRunning() {
        if (future != null && (!future.isCancelled())) {
            future.cancel(true);
            currentSchedulingRateInSeconds.set(0);
            isAutoDiscoveryInProgress.set(false);
            log.debug("Scheduler for auto discovery is stopped");
        }
    }

    private void filterIpForDeviceScan(String startIpAddress, String endIpAddress, Gateway gateway) {
        int timeoutInMilliseconds = config.getDevicePingTimeoutPeriod() * 1000;

        if (IPAddressUtil.isValidIPv4(startIpAddress) && IPAddressUtil.isValidIPv4(endIpAddress)) {
            log.debug("Received IP Address range is of type IPv4");
            IPAddressUtil startIp = new IPAddressUtil(startIpAddress);
            IPAddressUtil endIp = new IPAddressUtil(endIpAddress);
            do {
                startScanning(gateway, startIp.toString(), timeoutInMilliseconds);
                startIp = startIp.next();
            } while (!startIp.equals(endIp.next()));

        } else if (IPAddressUtil.isValidIPv6(startIpAddress.split("[/%]", 2)[0]) && IPAddressUtil.isValidIPv6(endIpAddress.split("[/%]", 2)[0])) {
            log.debug("Received IP Address range is of type IPv6");
            IPv6Address startIpv6Address = IPv6Address.fromString(startIpAddress.split("[/%]", 2)[0]);
            IPv6Address endIpv6Address = IPv6Address.fromString(endIpAddress.split("[/%]", 2)[0]);
            do {
                startScanning(gateway, startIpv6Address.toString(), timeoutInMilliseconds);
                startIpv6Address = startIpv6Address.add(1);
            } while (!startIpv6Address.equals(endIpv6Address.add(1)));
        } else {
            log.error("The IP address is invalid as it's neither of IPv4 or IPv6 type.");
        }
    }

    private void startScanning(Gateway gateway, String startIp, int timeoutInMilliseconds) {
        try {
            log.debug("Trying to reach the device with IP Address: " + startIp);
            if (InetAddress.getByName(startIp).isReachable(timeoutInMilliseconds)) {
                boolean isSnmpEnabled = isDeviceSnmpEnabled(startIp);
                if (!mapIpAddressToGid.containsKey(startIp) && isSnmpEnabled) {
                    log.debug("A new device is found with IP Address " + startIp + ", which is SNMP enabled.");
                    final Optional<ManagedObjectRepresentation> managedObjectOptional = inventoryRepository.save(gateway, managedObjectFactory.createChildDevice("Device-" + startIp, startIp));
                    if (managedObjectOptional.isPresent()) {
                        referenceChildDevice(gateway, managedObjectOptional.get().getId());
                    }
                } else if (!isSnmpEnabled) {
                    log.debug("A new device is found with IP Address " + startIp + ", which is not SNMP enabled.");
                    eventPublisher.publishEvent(new UnknownTrapOrDeviceEvent(gateway, new ConfigEventType(
                            "A new device is found with IP Address " + startIp + ", which is not SNMP enabled."), c8y_DeviceSnmpNotEnabled + startIp));
                }
            } else {
                if (mapIpAddressToGid.containsKey(startIp)) {
                    log.debug("No response from device with IP Address " + startIp + " during auto-discovery device scan.");
                    eventPublisher.publishEvent(new UnknownTrapOrDeviceEvent(gateway, new ConfigEventType(
                            "No response from device with IP Address " + startIp + " during auto-discovery device scan."), c8y_DeviceNotResponding + startIp));
                } else {
                    log.debug("The device with IP Address: " + startIp + " is unreachable");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
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

    private boolean isDeviceSnmpEnabled(String ipAddress) {
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);

        TransportMapping transport = null;
        TransportIpAddress transportIpAddress = null;
        try {
            transport = new DefaultUdpTransportMapping();
            transport.listen();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        Snmp snmp = new Snmp(transport);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setVersion(SnmpConstants.version1);

        String url = ipAddress + "/" + config.getPollingPort();
        if (config.getAddress().startsWith(TCP)) {
            target.setAddress(new TcpAddress(url));
            transportIpAddress = new TcpAddress(url);
        } else if (config.getAddress().startsWith(UDP)) {
            target.setAddress(new UdpAddress(url));
            transportIpAddress = new UdpAddress(url);
        }
        if (transportIpAddress != null) {
            target.setAddress(transportIpAddress);
        }

        try {
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if (response != null) {
                return true;
            }
        } catch (IOException e) {
            log.error("Exception while processing SNMP compatibility check", e);
        }

        return false;
    }

    @RunWithinContext
    private void createRegisteredDeviceMap(Gateway gateway) {
        final List<GId> currentDeviceIds = gateway.getCurrentDeviceIds();
        mapIpAddressToGid.clear();
        if (currentDeviceIds != null) {
            for (final GId gId : currentDeviceIds) {
                final Optional<ManagedObjectRepresentation> optional = inventoryRepository.get(gateway, gId);
                if (optional.isPresent()) {
                    final Optional<Device> deviceOptional = deviceFactory.convert(optional.get());
                    if (deviceOptional.isPresent()) {
                        final Device device = deviceOptional.get();
                        mapIpAddressToGid.put(device.getIpAddress(), device.getId());
                    }
                }
            }
        }
    }
}
