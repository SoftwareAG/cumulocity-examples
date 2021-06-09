/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors. 
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
