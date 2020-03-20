package com.cumulocity.agent.snmp.device.service;

import static com.cumulocity.agent.snmp.util.SnmpUtil.getAuthProtocolOid;
import static com.cumulocity.agent.snmp.util.SnmpUtil.getPrivacyProtocolOid;
import static com.cumulocity.agent.snmp.util.SnmpUtil.isValidAuthProtocol;
import static com.cumulocity.agent.snmp.util.SnmpUtil.isValidPrivacyProtocol;
import static com.cumulocity.agent.snmp.util.SnmpUtil.isValidSnmpVersion;
import static org.snmp4j.mp.MPv3.createLocalEngineID;
import static org.snmp4j.mp.SnmpConstants.version3;

import java.io.IOException;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.DeviceAuthentication;
import com.cumulocity.agent.snmp.util.IpAddressUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpDevicePoller {

	private DeviceManagedObjectWrapper deviceWrapper;

	private GatewayProperties.SnmpProperties snmpProperties;

	private List<VariableBinding> variableBindingList;

	private DeviceAuthentication deviceAuth;

	private PDU pdu;

	private Snmp snmp;

	private Target target;

	private TransportIpAddress address;

	private TransportMapping<?> transport;

	public SnmpDevicePoller(GatewayProperties.SnmpProperties snmpProperties, DeviceManagedObjectWrapper deviceWrapper,
			List<VariableBinding> variableBindingList) throws IOException {
		this.deviceWrapper = deviceWrapper;
		this.snmpProperties = snmpProperties;
		this.variableBindingList = variableBindingList;
		this.deviceAuth = deviceWrapper.getProperties().getAuth();

		init();
	}

	public ResponseEvent poll() throws IOException {
		return snmp.send(pdu, target);
	}

	private void init() throws IOException {
		if (!isValidSnmpVersion(deviceWrapper.getProperties().getVersion())) {
			log.error("Invalid SNMP Version assigned to device {}", deviceWrapper.getProperties().getIpAddress());
			return;
		}

		createTransportMapping();
		transport.listen();

		snmp = new Snmp(transport);

		createPDU();
	}

	private void createTransportMapping() throws IOException {
		String trapListenerBindingAddress = IpAddressUtil.sanitizeIpAddress(deviceWrapper.getProperties().getIpAddress(), true);
		String addressString = trapListenerBindingAddress + "/" + deviceWrapper.getProperties().getPort();

		if (snmpProperties.isTrapListenerProtocolTcp()) {
			address = new TcpAddress(addressString);
			transport = new DefaultTcpTransportMapping();
		} else {
			address = new UdpAddress(addressString);
			transport = new DefaultUdpTransportMapping();
		}
	}

	private void createPDU() {
		if (deviceWrapper.getProperties().getVersion() == version3) {
			USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(createLocalEngineID()), 0);
			SecurityModels.getInstance().addSecurityModel(usm);

			pdu = new ScopedPDU();

			OctetString engineId = new OctetString(deviceWrapper.getProperties().getAuth().getEngineId());
			((ScopedPDU) pdu).setContextEngineID(engineId);

			switch (deviceWrapper.getProperties().getAuth().getSecurityLevel()) {
				case SecurityLevel.NOAUTH_NOPRIV:
					target = getV3TargetForNoAuthNoPriv();
					break;
	
				case SecurityLevel.AUTH_NOPRIV:
					if (!isValidAuthProtocol(deviceWrapper.getProperties().getAuth().getAuthProtocol())) {
						log.error("Invalid authentication protocol provided for SNMP v3 device {} with security level AUTH_NOPRIV.", 
								deviceWrapper.getProperties().getIpAddress());
						return;
					}
	
					target = getV3TargetForAuthNoPriv();
					break;
	
				case SecurityLevel.AUTH_PRIV:
					if (!isValidAuthProtocol(deviceWrapper.getProperties().getAuth().getAuthProtocol())
							|| !isValidPrivacyProtocol(deviceWrapper.getProperties().getAuth().getPrivProtocol())) {
						log.error("Invalid authentication and/or privacy protocol provided for SNMP v3 device {} with security "
								+ "level AUTH_PRIV.", deviceWrapper.getProperties().getIpAddress());
						return;
					}
	
					target = getV3TargetForAuthPriv();
					break;
	
				default:
					log.error("Undefined Security level for SNMP v3 device {}.", deviceWrapper.getProperties().getIpAddress());
					return;
			}
		} else {
			pdu = new PDU();
			target = createV1V2Target(deviceWrapper.getProperties().getVersion(), address);
		}

		pdu.clear();
		pdu.setType(PDU.GET);
		variableBindingList.forEach(pdu::add);
	}

	private Target getV3TargetForNoAuthNoPriv() {
		String usernameStr = deviceAuth.getUsername();
		OctetString username = new OctetString(usernameStr);

		UsmUser user = new UsmUser(username, null, null, null, null);
		snmp.getUSM().addUser(new OctetString(username), user);

		return createV3Target(deviceWrapper.getProperties().getVersion(), address, username, SecurityLevel.NOAUTH_NOPRIV); 
	}

	private Target getV3TargetForAuthNoPriv() {
		String usernameStr = deviceAuth.getUsername();
		OctetString username = new OctetString(usernameStr);
		OctetString authPassword = new OctetString(deviceAuth.getAuthPassword());
		OID authProtocol = getAuthProtocolOid(deviceAuth.getAuthProtocol());
		
		UsmUser user = new UsmUser(username, authProtocol, authPassword, null, null);
		snmp.getUSM().addUser(username, user);

		return createV3Target(deviceWrapper.getProperties().getVersion(), address, username, SecurityLevel.AUTH_NOPRIV);
	}

	private Target getV3TargetForAuthPriv() {
		String usernameStr = deviceAuth.getUsername();
		OctetString username = new OctetString(usernameStr);
		
		OctetString authPassword = new OctetString(deviceAuth.getAuthPassword());
		OctetString privatePassword = new OctetString(deviceAuth.getPrivPassword());
		
		OID authProtocol = getAuthProtocolOid(deviceAuth.getAuthProtocol());
		OID privateProtocol = getPrivacyProtocolOid(deviceAuth.getPrivProtocol());

		UsmUser user = new UsmUser(username, authProtocol, authPassword, privateProtocol, privatePassword);
		snmp.getUSM().addUser(new OctetString(usernameStr), user);

		return createV3Target(deviceWrapper.getProperties().getVersion(), address, username, SecurityLevel.AUTH_PRIV);
	}

	private Target createV1V2Target(int snmpVersion, TransportIpAddress targetAddress) {
		CommunityTarget target = new CommunityTarget();
		target.setRetries(3);
		target.setTimeout(1000 * 5);
		target.setVersion(snmpVersion);
		target.setAddress(targetAddress);
		target.setCommunity(new OctetString(snmpProperties.getCommunityTarget()));

		return target;
	}

	private Target createV3Target(int snmpVersion, TransportIpAddress targetAddress, OctetString securityName, int securityLevel) {
		UserTarget target = new UserTarget();
		target.setRetries(3);
		target.setTimeout(1000 * 5);
		target.setVersion(snmpVersion);
		target.setAddress(targetAddress);
		target.setSecurityName(securityName);
		target.setSecurityLevel(securityLevel);

		return target;
	}

	public void close() {
		if (transport != null) {
			try {
				transport.close();
			} catch (IOException e) {
				log.error("Exception while closing TransportMapping ", e);
			}
		}

		if (snmp != null) {
			try {
				snmp.close();
			} catch (IOException e) {
				log.error("Exception while closing SNMP session ", e);
			}
		}
	}
}
