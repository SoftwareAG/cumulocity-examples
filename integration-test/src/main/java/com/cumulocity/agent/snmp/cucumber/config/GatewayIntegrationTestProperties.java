package com.cumulocity.agent.snmp.cucumber.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.validation.constraints.Pattern;

@Data
@Configuration
@PropertySource(value = "file:${user.home}/.snmp/cucumber.properties", ignoreResourceNotFound = true)
public class GatewayIntegrationTestProperties {

	private TenantProperties mgmtTenantProperties;

	@Value("#{'${gateway.identifier}'.trim()}")
	@Pattern(regexp = "(.|\\s)*\\S(.|\\s)*")
	private String gatewayIdentifier;

	@Value("#{'${C8Y.baseURL:http://developers.cumulocity.com}'.trim()}")
	@Pattern(regexp = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
	private String baseUrl;

	@Value("#{'${C8Y.forceInitialHost:true}'.trim()}")
	private boolean forceInitialHost;

	@Value("#{'${gateway.jar.location:systemProperties['user.home']/.snmp/snmp-agent-gateway.jar}'.trim()}")
	private String gatewayJarLocation;

	@Configuration
	@Data
	@ToString(exclude = "password")
	public class TenantProperties {

		@Value("#{'${C8Y.tenant}'.trim()}")
		private String tenantId;

		@Value("#{'${C8Y.user}'.trim()}")
		private String username;

		@Value("#{'${C8Y.password}'.trim()}")
		private String password;
	}
}