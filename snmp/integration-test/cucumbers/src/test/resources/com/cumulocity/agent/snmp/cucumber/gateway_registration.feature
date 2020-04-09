#
# Copyright © 2012 - 2017 Cumulocity GmbH.
# Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

@GatewayRegistration
Feature: Running gateway and auto-registration

  Background:
    Given Scenario tenant is created
    And I create gateway configuration

  Scenario: Test device registration with the platform
    Given There are no existing device credentials available locally
    And   There is no gateway device present in the platform
    When  I start the gateway process
    And   I register the gateway device
    And   I accept gateway device request
    Then  Gateway device should be created

  Scenario: Test gateway restart should not create new devices
    Given There are no existing device credentials available locally
    And   There is no gateway device present in the platform
    And   I start the gateway process
    And   I register the gateway device
    And   I accept gateway device request
    And   Gateway device is available
    When  I stop gateway process
    And   I start the gateway process
    Then  There should exist only one gateway device in the platform
    
  Scenario: Test device registration failure when device already created by another user
    Given There are no existing device credentials available locally
    And   There is no gateway device present in the platform
    And   I create a snmp gateway device using admin user
    When  I start the gateway process
    And   I register the gateway device
    And   I accept gateway device request
    Then  Gateway process should stop