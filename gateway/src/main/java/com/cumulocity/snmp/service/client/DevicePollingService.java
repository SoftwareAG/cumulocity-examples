package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.utils.SnmpAuthProtocol;
import com.cumulocity.snmp.utils.SnmpPrivacyProtocol;
import com.cumulocity.snmp.utils.SnmpVariableType;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DevicePollingService {

    @Autowired
    private SNMPConfigurationProperties config;

    public void initiatePolling(String oId, Device device, PduListener pduListener) {
        if (!isValidSnmpVersion(device.getSnmpVersion())) {
            log.error("Invalid SNMP Version assigned to device");
            return;
        }

        PDU pdu;
        AbstractTransportMapping transport = null;
        Snmp snmp = null;
        Target target;

        try {
            transport = new DefaultUdpTransportMapping();
            transport.listen();

            snmp = new Snmp(transport);
            TransportIpAddress address = new UdpAddress(device.getIpAddress() + "/" + device.getPort());

            if (device.getSnmpVersion() == SnmpConstants.version3) {
                if (!isValidUserName(device.getUsername())) {
                    log.error("Invalid entry. Should provide username for SNMP v3 device");
                    return;
                }
                pdu = new ScopedPDU();

                OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
                USM usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
                SecurityModels.getInstance().addSecurityModel(usm);

                switch (device.getSecurityLevel()) {
                    case SecurityLevel.NOAUTH_NOPRIV:
                        target = getV3TargetForNoAuthNoPriv(snmp, device, address);
                        break;

                    case SecurityLevel.AUTH_NOPRIV:
                        if (!isValidAuthProtocol(device.getAuthProtocol())) {
                            log.error("Invalid entry. Should provide valid authentication protocol for SNMP v3 device");
                            return;
                        }
                        if (device.getAuthProtocolPassword().length() < 8) {
                            log.error("Invalid entry. Auth password must be of at least 8 characters");
                            return;
                        }
                        target = getV3TargetForAuthNoPriv(snmp, device, address);
                        break;

                    case SecurityLevel.AUTH_PRIV:
                        if (!isValidAuthProtocol(device.getAuthProtocol())
                                || !isValidPrivacyProtocol(device.getPrivacyProtocol())) {
                            log.error("Invalid entry. Should provide both valid authentication and privacy protocol for SNMP v3 device");
                            return;
                        }
                        if (device.getAuthProtocolPassword().length() < 8 || device.getPrivacyProtocolPassword().length() < 8) {
                            log.error("Invalid entry. Auth and Privacy password must be for at least 8 characters");
                            return;
                        }
                        target = getV3TargetForAuthPriv(snmp, device, address);
                        break;

                    default:
                        log.error("Undefined Security level for SNMP v3");
                        return;
                }
            } else {
                pdu = new PDU();
                target = getTarget(device.getSnmpVersion(), address);
            }
            pdu.setType(PDU.GET);
            pdu.add(new VariableBinding(new OID(oId)));

            ResponseEvent responseEvent = snmp.send(pdu, target);
            handleDevicePollingResponse(responseEvent, oId, device.getIpAddress(), pduListener);
        } catch (IOException e) {
            log.error("Exception while processing SNMP Polling response ", e);
        } finally {
            closeTransport(transport);
            closeSnmp(snmp);
        }
    }

    private Target getV3TargetForAuthPriv(Snmp snmp, Device device, TransportIpAddress address) {
        UsmUser user = createUsmUser(
                new OctetString(SnmpAuthProtocol.getAuthProtocolName(device.getAuthProtocol())),
                SnmpAuthProtocol.getAuthProtocolOid(device.getAuthProtocol()),
                new OctetString(device.getAuthProtocolPassword()),
                SnmpPrivacyProtocol.getPrivacyProtocolOid(device.getPrivacyProtocol()),
                new OctetString(device.getPrivacyProtocolPassword()));
        snmp.getUSM().addUser(new OctetString(device.getUsername()), user);
        return getV3Target(device.getSnmpVersion(), address,
                SnmpAuthProtocol.getAuthProtocolName(device.getAuthProtocol()), SecurityLevel.AUTH_PRIV);
    }

    private Target getV3TargetForAuthNoPriv(Snmp snmp, Device device, TransportIpAddress address) {
        UsmUser user = createUsmUser(
                new OctetString(SnmpAuthProtocol.getAuthProtocolName(device.getAuthProtocol())),
                SnmpAuthProtocol.getAuthProtocolOid(device.getAuthProtocol()),
                new OctetString(device.getAuthProtocolPassword()),
                null, null);
        snmp.getUSM().addUser(new OctetString(device.getUsername()), user);
        return getV3Target(device.getSnmpVersion(), address,
                SnmpAuthProtocol.getAuthProtocolName(device.getAuthProtocol()), SecurityLevel.AUTH_NOPRIV);
    }

    private Target getV3TargetForNoAuthNoPriv(Snmp snmp, Device device, TransportIpAddress address) {
        UsmUser user = createUsmUser(new OctetString(SnmpAuthProtocol.SHA.getName()), null, null, null, null);
        snmp.getUSM().addUser(new OctetString(device.getUsername()), user);
        return getV3Target(device.getSnmpVersion(), address,
                SnmpAuthProtocol.SHA.getName(), SecurityLevel.NOAUTH_NOPRIV);
    }

    private UsmUser createUsmUser(OctetString securityName, OID authenticationProtocol,
                                  OctetString authenticationPassphrase, OID privacyProtocol, OctetString privacyPassphrase) {
        return new UsmUser(securityName, authenticationProtocol, authenticationPassphrase, privacyProtocol, privacyPassphrase);
    }

    private boolean isValidPrivacyProtocol(int privacyProtocol) {
        return privacyProtocol == SnmpPrivacyProtocol.DES.getValue()
                || privacyProtocol == SnmpPrivacyProtocol.AES128.getValue()
                || privacyProtocol == SnmpPrivacyProtocol.AES192.getValue()
                || privacyProtocol == SnmpPrivacyProtocol.AES256.getValue();
    }

    private boolean isValidAuthProtocol(int authenticationProtocol) {
        return authenticationProtocol == SnmpAuthProtocol.MD5.getValue()
                || authenticationProtocol == SnmpAuthProtocol.SHA.getValue();
    }

    private boolean isValidUserName(String username) {
        return (username != null) && (!username.isEmpty());
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
                || snmpVersion == SnmpConstants.version2c
                || snmpVersion == SnmpConstants.version3;
    }

    private boolean isValidVariableType(int type) {
        return type == SnmpVariableType.INTEGER.getType()
                || type == SnmpVariableType.COUNTER32.getType()
                || type == SnmpVariableType.GAUGE.getType()
                || type == SnmpVariableType.COUNTER64.getType();
    }

    private Target getTarget(int snmpVersion, TransportIpAddress targetAddress) {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(config.getCommunityTarget()));
        target.setAddress(targetAddress);
        target.setVersion(snmpVersion);
        target.setTimeout(1000 * 5);
        return target;
    }

    private Target getV3Target(int snmpVersion, TransportIpAddress targetAddress, String securityName, int securityLevel) {
        UserTarget target = new UserTarget();
        target.setAddress(targetAddress);
        target.setVersion(snmpVersion);
        target.setSecurityLevel(securityLevel);
        target.setSecurityName(new OctetString(securityName));
        target.setTimeout(1000 * 5);
        return target;
    }
}
