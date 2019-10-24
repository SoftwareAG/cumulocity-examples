package com.cumulocity.agent.snmp.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Data
@Configuration
@PropertySources(value = {
		@PropertySource(value = "file:${user.home}/.snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:${user.home}/.snmp/snmp-agent-gateway-${spring.profiles.active}.properties", ignoreResourceNotFound = true) })
public class GatewayProperties {

	@Autowired
	private BootstrapProperties bootstrapProperties;

	@Autowired
	private SnmpProperties snmpProperties;

	@Value("#{'${gateway.identifier:snmp-agent}'.trim()}")
	private String gatewayIdentifier;

	@Value("#{'${gateway.bootstrapFixedDelay:10000}'.trim()}")
	private int bootstrapFixedDelay;

	@Value("#{'${gateway.availability.interval:10}'.trim()}")
	private int gatewayAvailabilityInterval;

	@Value("#{'${gateway.objects.refresh.interval:1}'.trim()}")
	private int gatewayObjectRefreshIntervalInMinutes;

	@Value("#{'${gateway.threadPool.size:30}'.trim()}")
	private int gatewayThreadPoolSize;

	@Value("#{'${gateway.bootstrap.force:false}'.trim()}")
	private boolean forcedBootstrap;

	@Value("#{'${C8Y.baseURL:http://developers.cumulocity.com}'.trim()}")
	private String baseUrl;

	@Value("#{'${C8Y.forceInitialHost:true}'.trim()}")
	private boolean forceInitialHost;

	public int getThreadPoolSizeForTrapProcessing() {
		// Using 20% of the total threads configured for gateway to Trap listening
		int poolSize = getGatewayThreadPoolSize() * 20 / 100;
		return (poolSize <= 0) ? 2 : poolSize;
	}

	public int getThreadPoolSizeForScheduledTasks() {
		/*
		 * Using 80% of the total threads configured for gateway to internal
		 * publish/subscribe service, polling and auto-discovery
		 */
		int poolSize = getGatewayThreadPoolSize() * 80 / 100;
		return (poolSize <= 0) ? 8 : poolSize;
	}

	@Configuration
	@Data
	@ToString(exclude = "password")
	public class BootstrapProperties {

		@Value("#{'${C8Y.bootstrap.tenant:management}'.trim()}")
		private String tenantId;

		@Value("#{'${C8Y.bootstrap.user:devicebootstrap}'.trim()}")
		private String username;

		@Value("#{'${C8Y.bootstrap.password:}'.trim()}")
		private String password;
	}

	@Configuration
	@Data
	@ToString
	public class SnmpProperties {
		@Value("#{'${snmp.trapListener.protocol:UDP}'.trim()}")
		private String trapListenerProtocol;

		@Value("#{'${snmp.trapListener.port:6671}'.trim()}")
		private int trapListenerPort;

		@Value("#{'${snmp.trapListener.address:}'.trim()}")
		private String trapListenerAddress;

		@Value("#{'${snmp.community.target}'.trim()}")
		private String communityTarget;

		@Value("#{'${snmp.polling.port:161}'.trim()}")
		private int pollingPort;

		@Value("#{'${snmp.polling.version:0}'.trim()}")
		private int pollingVersion;

		@Value("#{'${snmp.autodiscovery.devicePingTimeoutPeriod:3}'.trim()}")
		private int autoDiscoveryDevicePingTimeoutPeriod;

		public boolean isTrapListenerProtocolUdp() {
			return "UDP".equalsIgnoreCase(trapListenerProtocol);
		}

		public boolean isTrapListenerProtocolTcp() {
			return "TCP".equalsIgnoreCase(trapListenerProtocol);
		}
	}
}