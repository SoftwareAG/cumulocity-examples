package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
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
import org.snmp4j.security.*;
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
        log.debug("Initiating SNMP Listner at address {}, and community {} ",
                config.getAddress(), config.getCommunityTarget());
        listen(new UdpAddress(config.getAddress() + "/" + config.getListenerPort()));
    }

    public synchronized void listen(TransportIpAddress snmpListeningAddress) {
        AbstractTransportMapping transportMapping;
        try {
            if (snmpListeningAddress instanceof TcpAddress) {
                transportMapping = new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);
            } else if (snmpListeningAddress instanceof UdpAddress) {
                transportMapping = new DefaultUdpTransportMapping((UdpAddress) snmpListeningAddress);
            } else {
                log.error("Received request format is not supported");
                return;
            }

            ThreadPool threadPool = ThreadPool.create("DispatcherPool", config.getThreadPoolSize());
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
            transportMapping.listen();
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

        log.debug("SNMP Data Received");

        String peerIPAddress = event.getPeerAddress().toString().split("/")[0];
        if (mapIPAddressToOid.containsKey(peerIPAddress)) {
            Map<String, PduListener> oidToPduListener = mapIPAddressToOid.get(peerIPAddress);
            for (VariableBinding var : pdu.getVariableBindings()) {
                if (oidToPduListener.containsKey(var.getOid().toString())) {
                    oidToPduListener.get(var.getOid().toString()).onPduReceived(pdu);
                }
            }
        } else {
            eventPublisher.publishEvent(new UnknownTrapRecievedEvent(gateway, new ConfigEventType(
                    "TRAP received from unknown device with IP Address : " + peerIPAddress)));
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

    public void initiatePolling(String oId, String ipAddress, int pollingPort,
                                int snmpVersion, PduListener pduListener) {
        if (!isValidSnmpVersion(snmpVersion)) {
            log.error("Invalid SNMP Version assigned to device");
            return;
        }

        PDU pdu = new PDU();
        AbstractTransportMapping transport = null;
        Snmp snmp = null;
        Target target;

        try {
            transport = new DefaultUdpTransportMapping();
            transport.listen();

            snmp = new Snmp(transport);
            target = getTarget(ipAddress.trim(), snmpVersion, pollingPort);
            pdu.setType(PDU.GET);
            pdu.add(new VariableBinding(new OID(oId)));

            ResponseEvent responseEvent = snmp.send(pdu, target);
            handleDevicePollingResponse(responseEvent, oId, ipAddress, pduListener);
        } catch (IOException e) {
            log.error("Exception while processing SNMP Polling response ", e);
        } finally {
            closeTransport(transport);
            closeSnmp(snmp);
        }
    }

    private void handleDevicePollingResponse(ResponseEvent responseEvent, String oId,
                                             String ipAddress, PduListener pduListener) {
        PDU response = responseEvent.getResponse();
        if (response == null) {
            log.error("Polling response null for device {} and OID {} - error:{} peerAddress:{} source:{} request:{}",
                    ipAddress, oId, responseEvent.getError(), responseEvent.getPeerAddress(),
                    responseEvent.getSource(), responseEvent.getRequest());
        } else if (response.getErrorStatus() == PDU.noError) {
            if (response.getVariableBindings().size() == 0) {
                log.error("No data found after successful device polling");
                return;
            }
            int type = response.getVariableBindings().get(0).getVariable().getSyntax();
            // Process polled data only if it is Integer32/Counter32/Gauge32/Counter64
            if (isValidVariableType(type)) {
                pduListener.onPduReceived(response);
            } else {
                log.error("Unsupported data format for measurement calculation");
            }
        } else {
            log.error("Error in Device polling response");
            log.error("Error index {} | Error status {} | Error text {} ",
                    response.getErrorIndex(), response.getErrorStatus(), response.getErrorStatusText());
        }
    }

    private void closeTransport(AbstractTransportMapping transport) {
        if (transport != null) {
            try {
                transport.close();
            } catch (IOException e) {
                log.error("IOException while closing TransportMapping ", e);
            }
        }
    }

    private void closeSnmp(Snmp snmp) {
        if (snmp != null) {
            try {
                snmp.close();
            } catch (IOException e) {
                log.error("IOException while closing SNMP connection ", e);
            }
        }
    }

    private boolean isValidSnmpVersion(int snmpVersion) {
        return snmpVersion == SnmpConstants.version1
                || snmpVersion == SnmpConstants.version2c;
    }

    private boolean isValidVariableType(int type) {
        return type == SnmpVariableType.INTEGER.toInt()
                || type == SnmpVariableType.COUNTER32.toInt()
                || type == SnmpVariableType.GAUGE.toInt()
                || type == SnmpVariableType.COUNTER64.toInt();
    }

    private Target getTarget(String ipAddress, int snmpVersion, int pollingPort) {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(config.getCommunityTarget()));
        target.setAddress(new UdpAddress(ipAddress + "/" + pollingPort));
        target.setVersion(snmpVersion);
        return target;
    }

    public enum SnmpVariableType {
        INTEGER(2), COUNTER32(65), GAUGE(66), COUNTER64(70);

        private int type;

        SnmpVariableType(int type) {
            this.type = type;
        }

        int toInt() {
            return type;
        }
    }
}
