package com.cumulocity.agent.snmp.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.validation.constraints.Pattern;

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

	@Value("#{'${gateway.bootstrap.force:false}'.trim()}")
	private boolean forcedBootstrap;

	@Value("#{'${C8Y.baseURL:http://developers.cumulocity.com}'.trim()}")
	@Pattern(regexp = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
	private String baseUrl;

	@Value("#{'${C8Y.forceInitialHost:true}'.trim()}")
	private boolean forceInitialHost;

	@Value("#{'${gateway.platform.connectionPool.max:25}'.trim()}")
	private Integer platformConnectionPoolMax;

	@Value("#{'${gateway.platform.connectionPool.perHost:15}'.trim()}")
	private Integer platformConnectionPoolPerHost;

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

		@Value("#{'${snmp.trapListener.protocol:}'.trim()}")
		private String trapListenerProtocol;

		@Value("#{'${snmp.trapListener.port:162}'.trim()}")
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

		@Value("#{'${snmp.trapListener.threadPoolSize:10}'.trim()}")
		private int trapListenerThreadPoolSize;
	}
}