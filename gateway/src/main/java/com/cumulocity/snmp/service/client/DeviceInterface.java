package com.cumulocity.snmp.service.client;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.factory.gateway.DeviceFactory;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.device.DeviceRemovedEvent;
import com.cumulocity.snmp.model.device.DeviceUpdatedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.utils.SnmpAuthProtocol;
import com.cumulocity.snmp.utils.SnmpPrivacyProtocol;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping.c8y_TRAPReceivedFromUnknownDevice;

@Slf4j
@Component
public class DeviceInterface implements CommandResponder {

    Map<String, Map<String, PduListener>> mapIPAddressToOid = new ConcurrentHashMap<>();

    Gateway gateway = new Gateway();
    Snmp snmp;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private SNMPConfigurationProperties config;
    @Autowired
    private DeviceFactory deviceFactory;
    @Autowired
    private ManagedObjectRepository managedObjectRepository;

    @PostConstruct
    public void init() {
        log.debug("Initiating SNMP Listner at address {}, port {} and community {} ",
                config.getAddress(), config.getListenerPort(), config.getCommunityTarget());
        listen(GenericAddress.parse(config.getAddress() + "/" + config.getListenerPort()));
    }

    public synchronized void listen(Address snmpListeningAddress) {
        AbstractTransportMapping transportMapping;
        try {
            if (snmpListeningAddress instanceof TcpAddress) {
                transportMapping = new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);
            } else if (snmpListeningAddress instanceof UdpAddress) {
                transportMapping = new DefaultUdpTransportMapping((UdpAddress) snmpListeningAddress);
            } else {
                log.error("Received address format is unsupported");
                return;
            }

            ThreadPool threadPool = ThreadPool.create("TrapListener", config.getThreadPoolSize());
            MessageDispatcher messageDispatcher = new MultiThreadedMessageDispatcher(threadPool,
                    new MessageDispatcherImpl());

            messageDispatcher.addMessageProcessingModel(new MPv1());
            messageDispatcher.addMessageProcessingModel(new MPv2c());

            SecurityProtocols.getInstance().addDefaultProtocols();
            final USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID(new OctetString())), 0);
            SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());
            SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES128());
            SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256());

            SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
            SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthSHA());

            usm.setEngineDiscoveryEnabled(true);

            messageDispatcher.addMessageProcessingModel(new MPv3(usm));
            snmp = new Snmp(messageDispatcher, transportMapping);
            SecurityModels.getInstance().addSecurityModel(usm);

            snmp.addCommandResponder(this);

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(config.getCommunityTarget()));

            log.debug("Listening for SNMP TRAPs on " + transportMapping.getListenAddress().toString());
            snmp.listen();
        } catch (IOException e) {
            log.error("Exception in SNMP TRAP listener ", e.getMessage(), e);
        }
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        PDU pdu = event.getPDU();

        if (pdu == null) {
            log.error("Received SNMP Data is null");
            return;
        }
        if (pdu.getVariableBindings() == null || pdu.getVariableBindings().size() == 0) {
            log.debug("No OID found in the received TRAP");
            return;
        }

        log.debug("SNMP Trap Received");

        String peerIPAddress = event.getPeerAddress().toString().split("/")[0];
        if (mapIPAddressToOid.containsKey(peerIPAddress)) {
            Map<String, PduListener> oidToPduListener = mapIPAddressToOid.get(peerIPAddress);
            for (VariableBinding var : pdu.getVariableBindings()) {
                if (oidToPduListener.containsKey(var.getOid().toString())) {
                    oidToPduListener.get(var.getOid().toString()).onPduReceived(pdu);
                }
            }
        } else {
            eventPublisher.publishEvent(new UnknownTrapOrDeviceEvent(gateway, new ConfigEventType(
                    "TRAP received from unknown device with IP Address : " + peerIPAddress), c8y_TRAPReceivedFromUnknownDevice + peerIPAddress));
        }
    }

    public void subscribe(Map<String, Map<String, PduListener>> mapIPAddressToOid) {
        this.mapIPAddressToOid = mapIPAddressToOid;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public void unsubscribe(String ipAddress) {
        mapIPAddressToOid.remove(ipAddress);
    }

    @EventListener
    @RunWithinContext
    public void updateSnmpV3Credentilas(final DeviceUpdatedEvent event) {
        final Optional<ManagedObjectRepresentation> optional = managedObjectRepository.get(event.getGateway(), event.getDeviceId());
        if (optional.isPresent()) {
            final Optional<Device> deviceOPtional = deviceFactory.convert(optional.get());
            if (deviceOPtional.isPresent()) {
                if (deviceOPtional.get().getSnmpVersion() == SnmpConstants.version3) {
                    addorUpdateSnmpV3Credentials(deviceOPtional.get());
                }
            }
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void addSnmpV3Credentilas(final DeviceAddedEvent event) {
        if (event.getDevice().getSnmpVersion() == SnmpConstants.version3) {
            addorUpdateSnmpV3Credentials(event.getDevice());
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void removeSnmpV3Credentials(final DeviceRemovedEvent event) {
        snmp.getUSM().removeAllUsers(new OctetString(event.getDevice().getUsername()),new OctetString(event.getDevice().getEngineId()));
    }

    private void addorUpdateSnmpV3Credentials(Device device) {
        if (snmp.getUSM().getUser(new OctetString(device.getEngineId()), new OctetString(device.getUsername())) != null) {
            snmp.getUSM().removeAllUsers(new OctetString(device.getUsername()), new OctetString(device.getEngineId()));
        }
        switch (device.getSecurityLevel()) {
            case SecurityLevel.NOAUTH_NOPRIV:
                snmp.getUSM().addUser(new OctetString(device.getUsername()), new OctetString(device.getEngineId()),
                        new UsmUser(new OctetString(device.getUsername()),
                                null,
                                null,
                                null,
                                null));
                break;

            case SecurityLevel.AUTH_NOPRIV:
                snmp.getUSM().addUser(new OctetString(device.getUsername()), new OctetString(device.getEngineId()),
                        new UsmUser(new OctetString(device.getUsername()),
                                SnmpAuthProtocol.getAuthProtocolOid(device.getAuthProtocol()),
                                new OctetString(device.getAuthProtocolPassword()),
                                null,
                                null));
                break;

            case SecurityLevel.AUTH_PRIV:
                snmp.getUSM().addUser(new OctetString(device.getUsername()), new OctetString(device.getEngineId()),
                        new UsmUser(new OctetString(device.getUsername()),
                                SnmpAuthProtocol.getAuthProtocolOid(device.getAuthProtocol()),
                                new OctetString(device.getAuthProtocolPassword()),
                                SnmpPrivacyProtocol.getPrivacyProtocolOid(device.getPrivacyProtocol()),
                                new OctetString(device.getPrivacyProtocolPassword())));
                break;

            default:
                log.error("Undefined Security level for SNMP v3");
                return;
        }
    }
}
