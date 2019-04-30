package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.*;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SNMPConfigurationProperties config;

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
            messageDispatcher.addMessageProcessingModel(new MPv3());

            SecurityProtocols.getInstance().addDefaultProtocols();
            SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

            Snmp snmp = new Snmp(messageDispatcher, transportMapping);
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
                    "TRAP received from unknown device with IP Address : " + peerIPAddress), c8y_TRAPReceivedFromUnknownDevice));
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
}
