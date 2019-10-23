package com.cumulocity.agent.snmp.device.service;

import c8y.RequiredAvailability;
import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.*;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.util.IpAddressUtil;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.InventoryMediaType;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * SNMP Device auto-discovery service
 */

@Slf4j
@Service
public class DeviceDiscoveryService {

    private static final String C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY = "c8y_SnmpAutoDiscovery";

    private static final String IP_RANGE_KEY = "ipRange";

    private static final short DEFAULT_CONNECTION_INTERVAL_IN_MINUTES = 10;

    @Autowired
    private GatewayProperties.SnmpProperties snmpProperties;

    @Autowired
    private GatewayDataProvider gatewayDataProvider;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private RestOperations restOperations;

    @Autowired
    private DeviceControlApi deviceControlApi;

    @Autowired
    private AlarmPublisher alarmPublisher;

    private ScheduledFuture<?> autoDiscoverySchedule;

    private String autoDiscoveryIpRanges;

    private List<InetAddress[]> autoDiscoveryIpRangesList;

    private long autoDiscoveryScheduleInterval;


    @EventListener(BootstrapReadyEvent.class)
    private void scheduleAutoDiscoveryProcess() {
        GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationPropertiesFromPlatform = gatewayDataProvider.getGatewayDevice().getSnmpCommunicationProperties();
        autoDiscoveryIpRanges = snmpCommunicationPropertiesFromPlatform.getIpRange();
        autoDiscoveryScheduleInterval = snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval();

        if(!Strings.isNullOrEmpty(autoDiscoveryIpRanges) && autoDiscoveryScheduleInterval > 0) {
            try {
                autoDiscoveryIpRangesList = parseIpRanges(autoDiscoveryIpRanges);
                autoDiscoverySchedule = taskScheduler.scheduleWithFixedDelay(() -> scanForSnmpDevicesAndCreateChildDevices(autoDiscoveryIpRangesList),
                        Duration.ofMinutes(gatewayDataProvider.getGatewayDevice().getSnmpCommunicationProperties().getAutoDiscoveryInterval()));
            } catch(IllegalArgumentException iae) {
                log.error("Error while parsing the provided <{}> IP ranges. Not scheduling the auto-discovery device scan.", autoDiscoveryIpRanges, iae);
            }
        }
    }

    @EventListener(GatewayDataRefreshedEvent.class)
    private void refreshAutoDiscoverySchedule() {
        GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties = gatewayDataProvider.getGatewayDevice().getSnmpCommunicationProperties();
        String newAutoDiscoveryIpRanges = snmpCommunicationProperties.getIpRange();
        long newAutoDiscoveryInterval = snmpCommunicationProperties.getAutoDiscoveryInterval();
        if(!StringUtils.equalsIgnoreCase(autoDiscoveryIpRanges, newAutoDiscoveryIpRanges) || autoDiscoveryScheduleInterval != newAutoDiscoveryInterval) {
            if(autoDiscoverySchedule != null) {
                autoDiscoverySchedule.cancel(true);
                autoDiscoverySchedule = null;
            }

            scheduleAutoDiscoveryProcess();
        }
    }

    @EventListener(OperationExecutedOnGatewayEvent.class)
    private void executeOperation(OperationExecutedOnGatewayEvent operationEvent) {

        OperationRepresentation operation = operationEvent.getOperationRepresentation();

        // Update the Platform with the operation status as Executing
        operation.setStatus(OperationStatus.EXECUTING.name());
        deviceControlApi.update(operation);

        String failureReason = null;
        try {
            Map<String, String> autoDiscoveryFragment = (Map<String, String>)operation.getAttrs().get(C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY);
            if(autoDiscoveryFragment != null && !Strings.isNullOrEmpty(autoDiscoveryFragment.get(IP_RANGE_KEY))) {
                String ipRanges = autoDiscoveryFragment.get(IP_RANGE_KEY);
                try {
                    scanForSnmpDevicesAndCreateChildDevices(parseIpRanges(ipRanges));
                } catch(IllegalArgumentException iae) {
                    failureReason = "Error while parsing the provided " + ipRanges + " IP Address ranges to scan for devices.";
                    log.error(failureReason, iae);
                }
            }
            else {
                failureReason = "Didn't provide the IP Address ranges to scan for devices.";
            }
        } catch(Throwable t) {
            failureReason = "Unexpected error occurred while scanning the network for SNMP devices.";
            log.error(failureReason, t);
        } finally {
            if(failureReason != null) {
                operation.setStatus(OperationStatus.FAILED.name());
                operation.setFailureReason(failureReason);
            }
            else {
                operation.setStatus(OperationStatus.SUCCESSFUL.name());
            }

            // Update the Platform with the operation status as Successful or Failed
            deviceControlApi.update(operation);
        }
    }

    private synchronized void scanForSnmpDevicesAndCreateChildDevices(List<InetAddress[]> ipRangesList) {
        Map<String, DeviceManagedObjectWrapper> childDevicesMap = gatewayDataProvider.getDeviceProtocolMap();

        int timeoutInMilliseconds = snmpProperties.getAutoDiscoveryDevicePingTimeoutPeriod() * 1000;
        for(InetAddress[] oneIpRange : ipRangesList) {
            InetAddress currentIp = oneIpRange[0];
            String currentIpString = currentIp.getHostAddress();
            InetAddress endIp = InetAddresses.increment(oneIpRange[1]);
            while(!currentIp.equals(endIp)) {
                try {
                    log.debug("Trying to find an SNMP device at IP Address <{}> during auto-discovery device scan.", currentIpString);

                    if (currentIp.isReachable(timeoutInMilliseconds)) {
                        boolean isDeviceSnmpEnabled = isDeviceSnmpEnabled(currentIp, snmpProperties.getPollingPort(), snmpProperties.isTrapListenerProtocolUdp());
                        if (isDeviceSnmpEnabled && !childDevicesMap.containsKey(currentIpString)) {
                            log.debug("A new SNMP enabled device is found with IP Address {} by auto-discovery device scan.", currentIpString);

                            // Create a new Child Device
                            createAndRegisterAChildDevice(currentIpString);
                        } else if (!isDeviceSnmpEnabled) {
                            handleNoResponseFromDevice("A device with IP Address <" + currentIpString + ">, which is not SNMP enabled, found during auto-discovery device scan.", AlarmMapping.c8y_DeviceSnmpNotEnabled + currentIpString);
                        }
                    } else {
                        if (childDevicesMap.containsKey(currentIpString)) {
                            handleNoResponseFromDevice("Existing SNMP device with IP Address <" + currentIpString + "> didn't respond during auto-discovery device scan.", AlarmMapping.c8y_DeviceNotResponding + currentIpString);
                        } else {
                            log.debug("No device is found at IP Address <{}> during auto-discovery device scan.", currentIpString);
                        }
                    }
                } catch (IOException e) {
                    // Ignore this exception and continue
                    log.info(e.getMessage(), e);
                }

                currentIp = InetAddresses.increment(currentIp);
            }
        }
    }

    private boolean isDeviceSnmpEnabled(InetAddress ipAddress, int pollingPort, boolean isTrapListenerProtocolUdp) {

        try {
            TransportMapping<?> transport;
            TransportIpAddress transportIpAddress;

            String url = ipAddress.getHostAddress() + "/" + String.valueOf(pollingPort);
            if(isTrapListenerProtocolUdp) {
                transportIpAddress = new UdpAddress(url);

                transport = new DefaultUdpTransportMapping();
            } else {
                transportIpAddress = new TcpAddress(url);

                transport = new DefaultTcpTransportMapping();
            }

            PDU pdu = new PDU();
            pdu.setType(PDU.GET);

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setVersion(SnmpConstants.version1);
            target.setAddress(transportIpAddress);

            transport.listen();
            Snmp snmp = new Snmp(transport);

            ResponseEvent responseEvent = snmp.send(pdu, target);

            return responseEvent.getResponse() != null;
        } catch (IOException e) {
            log.warn("Exception while processing SNMP compatibility check during auto-discovery device scan.", e);
        }

        return false;
    }

    private List<InetAddress[]> parseIpRanges(String ipRanges) throws IllegalArgumentException {
        String[] ipRangesSplitArray = ipRanges.split(",");

        List<InetAddress[]> ipRangesList = new ArrayList<>(ipRangesSplitArray.length);
        for(String oneIpRange : ipRangesSplitArray) {
            String[] oneIpRangeArray = oneIpRange.split("-");
            if(oneIpRangeArray.length == 2) {
                try {
                    InetAddress[] addresses = new InetAddress[] {
                            IpAddressUtil.forString(oneIpRangeArray[0]),
                            IpAddressUtil.forString(oneIpRangeArray[1])
                    };
                    ipRangesList.add(addresses);
                } catch(IllegalArgumentException iae) {
                    throw new IllegalArgumentException("Invalid range <" + oneIpRange + "> in the <" + ipRanges + "> IP ranges.", iae);
                }
            }
            else {
                throw new IllegalArgumentException("Invalid range <" + oneIpRange + "> in the <" + ipRanges + "> IP ranges.");
            }
        }

        return ipRangesList;
    }

    private void createAndRegisterAChildDevice(String deviceIpAddress) {
        log.error("Creating an SNMP child device with ID {}", "Device-" + deviceIpAddress);

        ManagedObjectRepresentation childDevice = new ManagedObjectRepresentation();
        try {
            // Create Child Device
            Map<String, String> deviceIpMap = new HashMap<>();
            deviceIpMap.put("deviceIpAddress", deviceIpAddress);
            deviceIpMap.put("port", String.valueOf(snmpProperties.getPollingPort()));

            childDevice.setName("Device-" + deviceIpAddress);
            childDevice.set(new RequiredAvailability(DEFAULT_CONNECTION_INTERVAL_IN_MINUTES));
            childDevice.setProperty(DeviceManagedObjectWrapper.C8Y_SNMP_DEVICE, deviceIpMap);

            childDevice = inventoryApi.create(childDevice);

            // Update the child device reference in the gateway device
            ManagedObjectReferenceRepresentation childReference = new ManagedObjectReferenceRepresentation();
            ManagedObjectRepresentation childMO = new ManagedObjectRepresentation();
            childMO.setId(childDevice.getId());
            childReference.setManagedObject(childMO);

            restOperations.post(gatewayDataProvider.getGatewayDevice().getChildDevicesPath(), InventoryMediaType.MANAGED_OBJECT_REFERENCE, childReference);
        } catch(SDKException sdke) {
            // Ignore and continue with the next IP, may be platform is unavailable
            // in which case this will be automatically resolved when the auto discovery runs next time.

            log.error("Error while creating the SNMP child device with ID {}", childDevice.getName(), sdke);
        }
    }

    private void handleNoResponseFromDevice(String type, String text) {
        log.debug(text + " An alarm published to the Platform.");

        AlarmMapping alarmMapping = new AlarmMapping();
        alarmMapping.setSeverity(AlarmSeverity.MAJOR.name());
        alarmMapping.setType(type);
        alarmMapping.setText(text);

        alarmPublisher.publish(alarmMapping.buildAlarmRepresentation(gatewayDataProvider.getGatewayDevice().getManagedObject()));
    }
}
