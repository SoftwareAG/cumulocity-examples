package com.cumulocity.agent.snmp.cucumber.steps;

import com.cumulocity.agent.snmp.cucumber.config.CucumberSpringContextConfiguration;
import com.cumulocity.agent.snmp.cucumber.config.TenantProvider;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CucumberSpringContextConfiguration.class)
public class PlatformSteps {

	@Autowired
	private TenantProvider tenantProvider;

	@Given("Scenario tenant is created")
	public void createTenant() {
		tenantProvider.createIntegrationTestTenant();
	}
}
