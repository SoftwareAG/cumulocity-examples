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

@devicePoller
Feature: Device poller scenarios

  Background:
    Given Scenario tenant is created

  Scenario Outline: Device protocol processing by device polling (version <version> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 1
    And I create snmp device protocol with JSON
    """
    {
        "type": "c8y_ModbusDeviceType",
        "fieldbusType": "snmp",
        "name": "Snmp device protocol",
        "c8y_Global": {},
        "c8y_IsDeviceType": {},
        "c8y_Registers": [{
            "name": "Trap",
            "measurementMapping": {
                "type": "c8y_Trap",
                "series": "T"
            },
            "eventMapping": {
                "type": "c8y_Trap",
                "text": "Trap event created"
            },
            "alarmMapping": {
                "type": "c8y_Trap",
                "text": "Trap alarm created",
                "severity": "WARNING"
            },
            "oid": "1.3.6.1.4.1.52032.1.1.2.0"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "<version model Id>"
    And I add last snmp device as child device to the gateway
    When I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025
    # Wait 65 seconds because the gatewayObjectRefreshIntervalInMinutes property is set to 1 minute
    And I wait for 65 seconds
    Then There should be 4-8 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 4-8 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 4-8, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

    Examples:
    |version model Id| version | protocol |
    | 0              | 1       | UDP      |
    | 1              | 2c      | UDP      |
    | 0              | 1       | TCP      |
    | 1              | 2c      | TCP      |

  Scenario Outline: Device protocol processing by device polling for version 3 (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 1
    And I create snmp device protocol with JSON
    """
    {
        "type": "c8y_ModbusDeviceType",
        "fieldbusType": "snmp",
        "name": "Snmp device protocol",
        "c8y_Global": {},
        "c8y_IsDeviceType": {},
        "c8y_Registers": [{
            "name": "Trap",
            "measurementMapping": {
                "type": "c8y_Trap",
                "series": "T"
            },
            "eventMapping": {
                "type": "c8y_Trap",
                "text": "Trap event created"
            },
            "alarmMapping": {
                "type": "c8y_Trap",
                "text": "Trap alarm created",
                "severity": "WARNING"
            },
            "oid": "1.3.6.1.4.1.52032.1.1.2.0"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025", version model Id "3" and authentication:
        | username  | privProtocol | privPassword      | authProtocol | authPassword    | securityLevel | engineId         |
        | adminUser | 1            | DESPrivPassword   | 1            | MD5AuthPassword | 3             | 49:U9:39:900:FJ8 |
    And I add last snmp device as child device to the gateway
    When I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025
    # Wait 65 seconds because the gatewayObjectRefreshIntervalInMinutes property is set to 1 minute
    And I wait for 65 seconds
    Then There should be 4-8 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 4-8 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 4-8, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Device protocol processing by device polling for different variables (version 1) (<variableType> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 1
    And I create snmp device protocol with JSON
    """
    {
        "type": "c8y_ModbusDeviceType",
        "fieldbusType": "snmp",
        "name": "Snmp device protocol",
        "c8y_Global": {},
        "c8y_IsDeviceType": {},
        "c8y_Registers": [{
            "name": "Trap",
            "measurementMapping": {
                "type": "c8y_Trap",
                "series": "T"
            },
            "eventMapping": {
                "type": "c8y_Trap",
                "text": "Trap event created"
            },
            "alarmMapping": {
                "type": "c8y_Trap",
                "text": "Trap alarm created",
                "severity": "WARNING"
            },
            "oid": "1.3.6.1.4.1.52032.1.1.5.0"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    When I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025 that has variable <variableType> with value <variableVal> and OId 1.3.6.1.4.1.52032.1.1.5.0
    # Wait 65 seconds because the gatewayObjectRefreshIntervalInMinutes property is set to 1 minute
    And I wait for 65 seconds
    Then There should be 4-8 measurement with type "c8y_Trap", fragmentType "c8y_Trap", series "T" and value "<result>" created for snmp device
    And There should be 4-8 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 4-8, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | variableType | variableVal | result | protocol |
    | Integer32    | 10          | 10     | UDP      |
    | Counter32    | 12          | 12     | UDP      |
    | TimeTicks    | 13          | 0.13   | UDP      |
    | Integer32    | 10          | 10     | TCP      |
    | Counter32    | 12          | 12     | TCP      |
    | TimeTicks    | 13          | 0.13   | TCP      |

  Scenario Outline: Device protocol processing by device polling for different variables (version 2c) (<variableType> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 1
    And I create snmp device protocol with JSON
    """
    {
        "type": "c8y_ModbusDeviceType",
        "fieldbusType": "snmp",
        "name": "Snmp device protocol",
        "c8y_Global": {},
        "c8y_IsDeviceType": {},
        "c8y_Registers": [{
            "name": "Trap",
            "measurementMapping": {
                "type": "c8y_Trap",
                "series": "T"
            },
            "eventMapping": {
                "type": "c8y_Trap",
                "text": "Trap event created"
            },
            "alarmMapping": {
                "type": "c8y_Trap",
                "text": "Trap alarm created",
                "severity": "WARNING"
            },
            "oid": "1.3.6.1.4.1.52032.1.1.5.0"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "1"
    And I add last snmp device as child device to the gateway
    When I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025 that has variable <variableType> with value <variableVal> and OId 1.3.6.1.4.1.52032.1.1.5.0
    # Wait 65 seconds because the gatewayObjectRefreshIntervalInMinutes property is set to 1 minute
    And I wait for 65 seconds
    Then There should be 4-8 measurement with type "c8y_Trap", fragmentType "c8y_Trap", series "T" and value "<result>" created for snmp device
    And There should be 4-8 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 4-8, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | variableType | variableVal | result | protocol |
    | Counter64    | 10          | 10     | UDP      |
    | Counter64    | 10          | 10     | TCP      |

  Scenario Outline: Device protocol processing by device polling for OctetString only processes event and alarms and not measurements (version 1 and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling port 1025
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 1
    And I create snmp device protocol with JSON
    """
    {
        "type": "c8y_ModbusDeviceType",
        "fieldbusType": "snmp",
        "name": "Snmp device protocol",
        "c8y_Global": {},
        "c8y_IsDeviceType": {},
        "c8y_Registers": [{
            "name": "Trap",
            "measurementMapping": {
                "type": "c8y_Trap",
                "series": "T"
            },
            "eventMapping": {
                "type": "c8y_Trap",
                "text": "Trap event created"
            },
            "alarmMapping": {
                "type": "c8y_Trap",
                "text": "Trap alarm created",
                "severity": "WARNING"
            },
            "oid": "1.3.6.1.4.1.52032.1.1.5.0"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    When I run snmp <protocol> simulation device with ip 127.0.0.1 and port 1025 that has variable <variableType> with value <variableVal> and OId 1.3.6.1.4.1.52032.1.1.5.0
    # Wait 65 seconds because the gatewayObjectRefreshIntervalInMinutes property is set to 1 minute
    And I wait for 65 seconds
    Then There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 4-8 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 4-8, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | variableType | variableVal | protocol |
    | OctetString  | 1011        | UDP      |
    | OctetString  | 1011        | TCP      |