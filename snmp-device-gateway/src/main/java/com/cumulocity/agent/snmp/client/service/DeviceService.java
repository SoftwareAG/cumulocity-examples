package com.cumulocity.agent.snmp.client.service;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.*;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.*;
import org.snmp4j.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.DeviceAuthentication;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.SnmpDeviceProperties;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeviceService {

	@Autowired
	private TrapHandler trapHandler;

	@Autowired
	private GatewayDataProvider gatewayDataProvider;

	@Autowired
	private GatewayProperties.SnmpProperties snmpProperties;

	@Getter
	private Snmp snmp = null;

	@EventListener(BootstrapReadyEvent.class)
	private void createSnmpListener() {
		try {
			configureUserSecurityModel();

			int poolSize = snmpProperties.getTrapListenerThreadPoolSize();
			ThreadPool threadPool = ThreadPool.create("snmp-dispatcher-thread", poolSize);
			MessageDispatcher msgdispatcher = new MessageDispatcherImpl();

			MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool, msgdispatcher);
			dispatcher.addMessageProcessingModel(new MPv1());
			dispatcher.addMessageProcessingModel(new MPv2c());
			dispatcher.addMessageProcessingModel(new MPv3());

			TransportMapping<? extends Address> transportMapping = createTransportMapping();

			snmp = new Snmp(dispatcher, transportMapping);
			snmp.addCommandResponder(trapHandler);
			snmp.listen();

			log.info("Started trap listening at {}", transportMapping.getListenAddress().toString());
		} catch (IOException ex) {
			log.error("Failed while staring the trap listener", ex);
		}
	}

	@EventListener(GatewayDataRefreshedEvent.class)
	private void refreshCredentials() {
		configureUserSecurityModel();
	}

	@PreDestroy
	public void stop() {
		try {
			if (snmp != null) {
				snmp.close();
			}
		} catch (IOException ex) {
			log.error("Failed to stop trap listener", ex);
		}
	}

	private void configureUserSecurityModel() {
		SecurityProtocols securityProtocols = SecurityProtocols.getInstance();
		securityProtocols.addDefaultProtocols();

		USM usm = new USM(securityProtocols, new OctetString(MPv3.createLocalEngineID()), 0);
		addCredentials(usm);

		SecurityModels.getInstance().addSecurityModel(usm);
	}

	private void addCredentials(USM usm) {
		Map<String, DeviceManagedObjectWrapper> deviceProtocolMap = gatewayDataProvider.getDeviceProtocolMap();
		deviceProtocolMap.forEach((deviceIP, managedObject) -> {
			SnmpDeviceProperties properties = managedObject.getProperties();

			if (properties.getVersion() == SnmpConstants.version3) {
				DeviceAuthentication authDetails = properties.getAuth();
				UsmUser user = createUser(authDetails);

				if (user != null) {
					OctetString userName = new OctetString(authDetails.getUsername());
					OctetString engineID = new OctetString(authDetails.getEngineId());
					usm.addUser(userName, engineID, user);
				}
			}
		});
	}

	private UsmUser createUser(DeviceAuthentication authDetails) {
		OctetString userName = new OctetString(authDetails.getUsername());

		switch (authDetails.getSecurityLevel()) {
		case SecurityLevel.NOAUTH_NOPRIV:
			return new UsmUser(userName, null, null, null, null);

		case SecurityLevel.AUTH_NOPRIV:
			OID authProtocolOid = getAuthProtocolOid(authDetails.getAuthProtocol());
			OctetString authPassword = new OctetString(authDetails.getAuthPassword());
			return new UsmUser(userName, authProtocolOid, authPassword, null, null);

		case SecurityLevel.AUTH_PRIV:
			authProtocolOid = getAuthProtocolOid(authDetails.getAuthProtocol());
			authPassword = new OctetString(authDetails.getAuthPassword());
			OID privacyProtocolOid = getPrivacyProtocolOid(authDetails.getPrivProtocol());
			OctetString privacyPassword = new OctetString(authDetails.getPrivPassword());
			return new UsmUser(userName, authProtocolOid, authPassword, privacyProtocolOid, privacyPassword);

		default:
			log.error("Unsupported {} Security level found in {} user configured for device having {} as engine id",
					authDetails.getSecurityLevel(), userName, authDetails.getEngineId());
			return null;
		}
	}

	private OID getAuthProtocolOid(int id) {
		switch (id) {
		case 1:
			return AuthMD5.ID;
		case 2:
			return AuthSHA.ID;
		default:
			log.error("Unsupported {} authentication protocol selected. Supported protocols are "
					+ "usmHMACMD5AuthProtocol as MD5 and usmHMACSHAAuthProtocol as SHA", id);
			return null;
		}
	}

	private OID getPrivacyProtocolOid(int id) {
		switch (id) {
		case 1:
			return PrivDES.ID;
		case 2:
			return PrivAES128.ID;
		case 3:
			return PrivAES192.ID;
		case 4:
			return PrivAES256.ID;
		default:
			log.error("Unsupported {} privacy protocol id found. Supported ones are "
					+ "1 for DES, 2 for AES128, 3 for AES192 and 4 for AES256", id);
			return null;
		}
	}

	private TransportMapping<? extends Address> createTransportMapping() throws IOException {
		String addStr = snmpProperties.getTrapListenerProtocol() + ":" + snmpProperties.getTrapListenerAddress() + "/"
				+ snmpProperties.getTrapListenerPort();
		Address snmpListeningAddress = GenericAddress.parse(addStr);

		if (snmpListeningAddress instanceof TcpAddress) {
			return new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);
		} else if (snmpListeningAddress instanceof UdpAddress) {
			return new DefaultUdpTransportMapping((UdpAddress) snmpListeningAddress);
		} else {
			String msg = "Unable to service snmp devices. Unsupported " + snmpProperties.getTrapListenerProtocol()
					+ " protocol selected. " + "Currently supported protocols are TCP and UDP.";
			throw new ProtocolException(msg);
		}
	}
}
