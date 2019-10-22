package com.cumulocity.agent.snmp.device.service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.DeviceAuthentication;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.SnmpDeviceProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceProtocolManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.util.IpAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.BindException;
import java.net.ProtocolException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class DeviceListenerService {

	@Autowired
	private GatewayProperties.SnmpProperties snmpProperties;

	@Autowired
	private GatewayDataProvider gatewayDataProvider;

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private TrapHandler trapHandler;

	Snmp snmp = null;

	private ScheduledFuture<?> snmpDevicePoller;

	private long pollingRateInMinutes = -1;

	@EventListener(BootstrapReadyEvent.class)
	private void onBootstrapReady() {
		createSnmpDeviceListener();

		createSnmpDevicePoller();
	}

	@EventListener(GatewayDataRefreshedEvent.class)
	private void onGatewayDataRefresh() {
		// Re-Configure user security model
		configureUserSecurityModel();

		// Re-schedule poller (only if configuration changed
		createSnmpDevicePoller();
	}

	protected void createSnmpDeviceListener() {
		try {
			configureUserSecurityModel();

			int poolSize = snmpProperties.getTrapListenerThreadPoolSize();
			ThreadPool threadPool = ThreadPool.create("snmp-dispatcher-thread", poolSize);
			MessageDispatcher messageDispatcher = new MessageDispatcherImpl();

			MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,
					messageDispatcher);
			dispatcher.addMessageProcessingModel(new MPv1());
			dispatcher.addMessageProcessingModel(new MPv2c());
			dispatcher.addMessageProcessingModel(new MPv3());

			String trapListenerBindingAddress = IpAddressUtil.sanitizeIpAddress(snmpProperties.getTrapListenerAddress(),
					true);
			TransportMapping<? extends Address> transportMapping = createTransportMapping(
					snmpProperties.getTrapListenerProtocol(), snmpProperties.getTrapListenerPort(),
					trapListenerBindingAddress);

			snmp = new Snmp(dispatcher, transportMapping);
			snmp.addCommandResponder(trapHandler);
			snmp.listen();

			log.info("Started listening to traps at {}", transportMapping.getListenAddress().toString());
		} catch (BindException be) {
			log.error("Failed to start listening to traps. Port {}/{} is already in use.\n"
					+ "Update the 'snmp.trapListener.port' and 'snmp.trapListener.address' properties in file:${user.home}/.snmp/snmp-agent-gateway.properties and restart the agent. "
					+ "Shutting down the agent...", snmpProperties.getTrapListenerAddress(),
					snmpProperties.getTrapListenerPort(), be);
			System.exit(0);
		} catch (IOException | IllegalArgumentException t) {
			log.error("Failed to start listening to traps on port {}/{}.\n"
					+ "Update the 'snmp.trapListener.port' and 'snmp.trapListener.address' properties in file:${user.home}/.snmp/snmp-agent-gateway.properties and restart the agent. "
					+ "Shutting down the agent...", snmpProperties.getTrapListenerAddress(),
					snmpProperties.getTrapListenerPort(), t);
			System.exit(0);
		}
	}

	protected void createSnmpDevicePoller() {
		long newPollingRateInMinutes = gatewayDataProvider.getGatewayDevice().getSnmpCommunicationProperties()
				.getPollingRate();

		if (snmpDevicePoller != null && !snmpDevicePoller.isDone() && pollingRateInMinutes == newPollingRateInMinutes) {
			return;
		}

		if (snmpDevicePoller != null) {
			snmpDevicePoller.cancel(true);
		}

		pollingRateInMinutes = newPollingRateInMinutes;

		if(pollingRateInMinutes <= 0) {
			return;
		}
		snmpDevicePoller = taskScheduler.scheduleWithFixedDelay(() -> {
			try {
				Map<String, DeviceManagedObjectWrapper> deviceProtocolMap = gatewayDataProvider.getDeviceProtocolMap();
				Map<String, DeviceProtocolManagedObjectWrapper> protocolMap = gatewayDataProvider.getProtocolMap();

				deviceProtocolMap.forEach((deviceIp, deviceWrapper) -> {
					String deviceProtocolName = deviceWrapper.getDeviceProtocol();

					if (protocolMap.containsKey(deviceProtocolName)) {
						DeviceProtocolManagedObjectWrapper deviceProtocolWrapper = protocolMap.get(deviceProtocolName);
						List<VariableBinding> varibleBindingList = deviceProtocolWrapper
								.getMeasurementVariableBindingList();

						if (varibleBindingList.size() > 0) {

							taskScheduler.schedule(() -> {
								SnmpDevicePoller devicePoller = null;

								try {
									devicePoller = new SnmpDevicePoller(snmpProperties, deviceWrapper,
											varibleBindingList);
									ResponseEvent responseEvent = devicePoller.poll();

									PDU responsePDU = responseEvent.getResponse();
									if (responsePDU == null) {
										log.error("Empty response was received while polling for device {} OIDs : {}",
												deviceIp, responseEvent.getRequest());
									} else if (responsePDU.getErrorStatus() == PDU.noError) {
										if (responsePDU.getVariableBindings().size() == 0) {
											log.error("No data found after successful device polling");
											return;
										}

										trapHandler.processDevicePdu(deviceIp, responsePDU);
									} else {
										log.error(
												"Error while polling device {} OIDs.\n"
														+ "Error index {} | Error status {} | Error text {} ",
												deviceIp, responsePDU.getErrorIndex(), responsePDU.getErrorStatus(),
												responsePDU.getErrorStatusText());
									}

								} catch (IOException e) {
									log.error("Failed while polling variables for the device {} with {} protocol",
											deviceIp, deviceWrapper.getDeviceProtocol(), e);
								} finally {
									if (devicePoller != null) {
										devicePoller.close();
									}
								}
							}, Instant.now());

						}
					} else {
						log.debug("{} device is not configured with device protocol", deviceIp);
					}
				});
			} catch (Throwable t) {
				log.error("Error while polling SNMP devices.", t);
			}
		}, Duration.ofMinutes(pollingRateInMinutes));
	}

	@PreDestroy
	private void stop() {
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
				UsmUser usmUser = createUser(authDetails);

				if (usmUser != null) {
					OctetString userName = new OctetString(authDetails.getUsername());
					OctetString engineID = new OctetString(authDetails.getEngineId());
					usm.addUser(userName, engineID, usmUser);
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

	TransportMapping<? extends Address> createTransportMapping(String protocol, int port, String bindingAddress)
			throws IOException {
		Address snmpListeningAddress = GenericAddress.parse(protocol + ":" + bindingAddress + "/" + port);

		if (snmpListeningAddress instanceof TcpAddress) {
			return new DefaultTcpTransportMapping((TcpAddress) snmpListeningAddress);
		} else if (snmpListeningAddress instanceof UdpAddress) {
			return new DefaultUdpTransportMapping((UdpAddress) snmpListeningAddress);
		} else {
			String msg = "Unable to service snmp devices. Unsupported " + protocol + " protocol selected. "
					+ "Currently supported protocols are TCP and UDP.";
			log.error(msg);

			throw new ProtocolException(msg);
		}
	}
}
