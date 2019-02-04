package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapRecievedEvent;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TrapListener implements CommandResponder {

    @Autowired
    private GatewayConfigurationProperties config;

    Map<String,Map<String,PduListener>> mapIPAddressToOid = new HashMap<>();

    Gateway gateway = new Gateway();

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {
      listen(new UdpAddress(config.getAddress()));
    }

    public synchronized void listen(TransportIpAddress snmpListeningAddress) {
        AbstractTransportMapping transportMapping;
        try {
            if (snmpListeningAddress instanceof TcpAddress) {
                transportMapping = new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);

            } else
                transportMapping = new DefaultUdpTransportMapping((UdpAddress) snmpListeningAddress);

            ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
            MessageDispatcher messageDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

            messageDispatcher.addMessageProcessingModel(new MPv1());
            messageDispatcher.addMessageProcessingModel(new MPv2c());
            messageDispatcher.addMessageProcessingModel(new MPv3());

            SecurityProtocols.getInstance().addDefaultProtocols();
            SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

            Snmp snmp = new Snmp(messageDispatcher, transportMapping);
            snmp.addCommandResponder(this);

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));

            transportMapping.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        PDU pdu = event.getPDU();

        if (pdu == null) {
            log.error("Received PDU is null");
            return;
        }

        log.info("Received PDU is : ",pdu);

        String peerIPAddress = event.getPeerAddress().toString().split("/")[0];
        if(mapIPAddressToOid.containsKey(peerIPAddress)){

            Map<String,PduListener> oidToPduListener = mapIPAddressToOid.get(peerIPAddress);

            if(oidToPduListener.containsKey(pdu.getVariableBindings().get(3).getOid().toString())){
                oidToPduListener.get(pdu.getVariableBindings().get(3).getOid().toString()).onPduRecived(pdu);
            }
        } else{
            eventPublisher.publishEvent(new UnknownTrapRecievedEvent(gateway, new ConfigEventType("TRAP received from unknown device with IP Address : "+peerIPAddress)));
        }

    }

    public void subscribe(Map<String,Map<String,PduListener>> mapIPAddressToOid){

        this.mapIPAddressToOid = mapIPAddressToOid;
    }

    public void setGateway(Gateway gateway){
        this.gateway = gateway;
    }

    public void unsubscribe(String ipAddress){
        if(mapIPAddressToOid.containsKey(ipAddress)) {
            mapIPAddressToOid.remove(ipAddress);
        }
    }
}
