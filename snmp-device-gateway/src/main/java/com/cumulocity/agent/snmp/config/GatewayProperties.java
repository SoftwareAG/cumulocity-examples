package com.cumulocity.agent.snmp.config;

import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import lombok.Data;
import lombok.ToString;

@Data
@Configuration
@PropertySources(value = {
		@PropertySource(value = "file:${user.home}/.snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:/etc/snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = true) })
public class GatewayProperties {

	@Value("${gateway.identifier}")
	private String gatewayIdentifier;

	@Value("${gateway.bootstrapFixedDelay:10000}")
	private int bootstrapFixedDelay;

	@Value("${gateway.availability.interval:10}")
	private int gatewayAvailabilityInterval;

	@Value(("${gateway.bootstrap.force:false}"))
	private boolean forcedBootstrap;

	@Value("${C8Y.baseURL}")
	@Pattern(regexp = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
	private String baseUrl;

	@Value("${C8Y.forceInitialHost:true}")
	private boolean forceInitialHost;

	@Value("${gateway.platform.connectionPool.max:25}")
	private Integer platformConnectionPoolMax;

	@Value("${gateway.platform.connectionPool.perHost:15}")
	private Integer platformConnectionPoolPerHost;

	@Autowired
	private DeviceBootstrapProperties bootstrapProperties;

	@Autowired
	private SnmpProperties snmpProperties;

	@Configuration
	@Data
	@ToString(exclude = "password")
	public class DeviceBootstrapProperties {

		@Value("${C8Y.bootstrap.tenant}")
		private String tenantId;

		@Value("${C8Y.bootstrap.user}")
		private String username;

		@Value("${C8Y.bootstrap.password}")
		private String password;

		@Value("${gateway.bootstrapFixedDelay:10000}")
		private Long bootstrapDelay;
	}

	@Configuration
	@Data
	@ToString
	public class SnmpProperties {

		@Value("${snmp.trapListener.protocol}")
		private String trapListenerProtocol;

		@Value("${snmp.trapListener.port}")
		private int trapListenerPort;

		@Value("${snmp.trapListener.address}")
		private String trapListenerAddress;

		@Value("${snmp.community.target}")
		private String communityTarget;

		@Value("${snmp.polling.port}")
		private int pollingPort;

		@Value("${snmp.polling.version}")
		private int pollingVersion;

		@Value("${snmp.trapListener.threadPoolSize}")
		private int trapListenerThreadPoolSize;

		@Value("${snmp.autodiscovery.devicePingTimeoutPeriod}")
		private int autoDiscoveryDevicePingTimeoutPeriod;
	}
}