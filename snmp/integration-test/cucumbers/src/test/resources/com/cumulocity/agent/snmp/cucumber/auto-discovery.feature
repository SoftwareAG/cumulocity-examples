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

@autoDiscovery
Feature: Auto-discovery scenarios

  Background:
    Given Scenario tenant is created

  Scenario Outline: Auto-discovery process triggered using auto discovery operation (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    # Wait 65 seconds because the realtime operation subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025
    When I create snmp auto discovery operation on gateway with ip range 127.0.0.1-127.0.0.3
    And I wait until last operation is successful on gateway with timeout 10 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Auto-discovery process triggered using auto discovery operation for Ipv6 (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    # Wait 65 seconds because the realtime operation subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp <protocol> simulation device with ip 0:0:0:0:0:0:0:1 and port 1025
    When I create snmp auto discovery operation on gateway with ip range 0:0:0:0:0:0:0:1
    And I wait until last operation is successful on gateway with timeout 10 seconds
    Then There should be a snmp device with ip 0:0:0:0:0:0:0:1 and port 1025 created as child on gateway

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Auto-discovery process triggered with scheduled job (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    # Wait 65 seconds because the realtime subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025
    When I set snmp gateway configuration with ipRange 127.0.0.1-127.0.0.3 and autoDiscoveryInterval 1
    And I wait for 60 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Auto-discovery process triggered with scheduled job for Ipv6 (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    # Wait 65 seconds because the realtime subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp <protocol> simulation device with ip 0:0:0:0:0:0:0:1 and port 1025
    When I set snmp gateway configuration with ipRange 0:0:0:0:0:0:0:1 and autoDiscoveryInterval 1
    And I wait for 65 seconds
    Then There should be a snmp device with ip 0:0:0:0:0:0:0:1 and port 1025 created as child on gateway

  Examples:
    | protocol |
    | UDP      |
    | TCP      |