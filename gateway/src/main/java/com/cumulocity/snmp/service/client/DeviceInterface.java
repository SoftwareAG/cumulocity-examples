package com.cumulocity.snmp.service.client;

import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.OperationRepository;
import com.cumulocity.snmp.service.autodiscovery.AutoDiscoveryService;
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

    private static final String CHILD_DEVICES_PATH = "/inventory/managedObjects/{deviceId}/childDevices";

    @PostConstruct
    public void init() {
        try {
            log.debug("Initiating SNMP Listner at address {}, and community {} ",
                    config.getAddress(), config.getCommunityTarget());
            listen(new TcpAddress(config.getAddress() + "/" + config.getListenerPort()));
        } catch (IOException e) {
            log.error("Exception initiating SNMP TRAP listener ", e);
        }
    }

    public synchronized void listen(TransportIpAddress snmpListeningAddress) throws IOException {
        AbstractTransportMapping transportMapping;
        try {
            if (snmpListeningAddress instanceof TcpAddress) {
                transportMapping = new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);
            } else {
                log.error("Received request is other than TCP");
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

            transportMapping.listen();
            log.debug("Listening for SNMP TRAPs on " + transportMapping.getListenAddress().toString());
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
            eventPublisher.publishEvent(new UnknownTrapOrDeviceEvent(gateway, new ConfigEventType(
                    "TRAP received from unknown device with IP Address : " + peerIPAddress),c8y_TRAPReceivedFromUnknownDevice));
        }
    }

    public void initiatePolling(String oId, String ipAddress, PduListener pduListener) throws IOException {
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(new OID(oId)));

        AbstractTransportMapping transport = null;
        Snmp snmp = null;

        try {
            transport = new DefaultTcpTransportMapping();
            transport.listen();

            snmp = new Snmp(transport);

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(config.getCommunityTarget()));
            target.setVersion(SnmpConstants.version2c);

            //TODO: Port has to be obtained from UI/User if required.
            target.setAddress(new TcpAddress(ipAddress + "/" + config.getPollingPort()));

            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if (response == null) {
                log.error("Polling response null for device {} and OID {} - error:{} peerAddress:{} source:{} request:{}",
                        ipAddress, oId,
                        responseEvent.getError(),
                        responseEvent.getPeerAddress(),
                        responseEvent.getSource(),
                        responseEvent.getRequest());
            } else if (response.getErrorStatus() == PDU.noError) {
                // Process polled data only if it is Integer
                if (response.getVariableBindings().get(0).getVariable().getSyntax() == 2) {
                    pduListener.onPduReceived(response);
                }
            } else {
                log.error("Error in Device polling response");
                log.error("Error index {} | Error status {} | Error text {} ",
                        response.getErrorIndex(), response.getErrorStatus(), response.getErrorStatusText());
            }
        } catch (IOException e) {
            log.error("Exception while processing SNMP Polling response ", e);
        } finally {
            if (transport != null) {
                transport.close();
            }
            if (snmp != null) {
                snmp.close();
            }
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
