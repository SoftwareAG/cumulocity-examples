/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
