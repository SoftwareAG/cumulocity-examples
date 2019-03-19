package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapRecievedEvent;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DeviceInterface implements CommandResponder {

    Map<String, Map<String, PduListener>> mapIPAddressToOid = new ConcurrentHashMap<>();

    Gateway gateway = new Gateway();

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private GatewayConfigurationProperties config;

    @PostConstruct
    public void init() {
        listen(new TcpAddress(config.getAddress()));
    }

    public synchronized void listen(TransportIpAddress snmpListeningAddress) {
        AbstractTransportMapping transportMapping;
        try {
            if (snmpListeningAddress instanceof TcpAddress) {
                transportMapping = new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);
            } else {
                log.error("Received request is other than TCP");
                return;
//                transportMapping = new DefaultUdpTransportMapping((UdpAddress) snmpListeningAddress);
            }

            ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
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

            transportMapping.listen();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        PDU pdu = event.getPDU();

        if (pdu == null) {
            log.error("Received SNMP Data is null");
            return;
        }

        log.debug("SNMP Data Received");

        String peerIPAddress = event.getPeerAddress().toString().split("/")[0];
        if (mapIPAddressToOid.containsKey(peerIPAddress)) {
            Map<String, PduListener> oidToPduListener = mapIPAddressToOid.get(peerIPAddress);
            for (VariableBinding var : pdu.getVariableBindings()) {
                if (oidToPduListener.containsKey(var.getOid().toString())) {
                    oidToPduListener.get(var.getOid().toString()).onPduRecived(pdu);
                }
            }
        } else {
            eventPublisher.publishEvent(new UnknownTrapRecievedEvent(gateway, new ConfigEventType(
                    "TRAP received from unknown device with IP Address : " + peerIPAddress)));
        }

    }

    public void initiatePolling(String oId, String ipAddress, PduListener pduListener) throws IOException {
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(new OID(oId)));

        TransportMapping transport = new DefaultTcpTransportMapping();
        transport.listen();

        Snmp snmp = new Snmp(transport);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(config.getCommunityTarget()));
        target.setVersion(SnmpConstants.version2c);

        //TODO: Port has to be obtained from UI/User if required.
        target.setAddress(new TcpAddress(ipAddress + "/" + 162));
        target.setRetries(2);
        target.setTimeout(5000);

        try {
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if (response == null) {
                log.error("Polling response null - error:{} peerAddress:{} source:{} request:{}",
                        responseEvent.getError(),
                        responseEvent.getPeerAddress(),
                        responseEvent.getSource(),
                        responseEvent.getRequest());
            } else {
                // Process polled data only if it is Integer
                if (response.getVariableBindings().get(0).getVariable().getSyntax() == 2) {
                    pduListener.onPduRecived(response);
                }
            }
        } catch (IOException e) {
            log.error("Exception while processing SNMP Polling response ", e);
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
