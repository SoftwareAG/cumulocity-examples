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

@trapProcessing
Feature: Trap-processing scenarios

  Background:
    Given Scenario tenant is created

  Scenario Outline: Processing trap with version 1 (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send <protocol> trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Processing trap with version 2c (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "1"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send <protocol> trap message with trap version 2c and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Processing trap with version 3 (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025", version model Id "3" and authentication:
        | username | authProtocol | authPassword   | privProtocol | privPassword      | securityLevel | engineId         |
        | username | 1            | authpassphrase | 2            | privacypassphrase | 3             | 49:U9:39:900:FJ8 |
    And I add last snmp device as child device to the gateway
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 120
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send <protocol> trap message with trap version 3 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario: Device with version 3 and invalid authentication password setting and trap processing
    Given I start and register gateway with UDP protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025", version model Id "3" and authentication:
        | username | authProtocol | authPassword          | privProtocol | privPassword      | securityLevel | engineId         |
        | username | 1            | authpassphraseInvalid | 2            | privacypassphrase | 3             | 49:U9:39:900:FJ8 |
    And I add last snmp device as child device to the gateway
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 120
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send UDP trap message with trap version 3 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 0 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be no alarm with type "c8y_Trap" and text "Trap alarm created" created for snmp device

  Scenario Outline: Trap processing without given OID (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send <protocol> trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.3
    And I wait for 5 seconds
    Then There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 0 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be no alarm with type "c8y_Trap" and text "Trap alarm created" created for snmp device

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario Outline: Alarm creation if the device is unknown to gateway and do not process trap (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send <protocol> trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be an alarm with count 1, type "c8y_TRAPReceivedFromUnknownDevice-127.0.0.1", text "Trap received from an unknown device with IP address : 127.0.0.1" and severity "MAJOR" created for gateway
    And There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 0 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be no alarm with type "c8y_Trap" and text "Trap alarm created" created for snmp device

  Examples:
    | protocol |
    | UDP      |
    | TCP      |

  Scenario: Verify processing of unmatching trap version with device version and update trap version on runtime
    Given I start and register gateway with UDP protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds

    When I send UDP trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

    When I send UDP trap message with trap version 3 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    # The output objects count should not be incremented
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

    Given I update the last snmp device with version model Id "3" and authentication:
        | username | authProtocol | authPassword   | privProtocol | privPassword      | securityLevel | engineId         |
        | username | 1            | authpassphrase | 2            | privacypassphrase | 3             | 49:U9:39:900:FJ8 |
    And I set snmp gateway configuration with ipRange 127.0.0.1, autoDiscoveryInterval 5 and polling rate 120
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    When I send UDP trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 2 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 2 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 2, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

    When I send UDP trap message with trap version 3 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 3 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 3 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 3, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  # Reference: https://tools.ietf.org/html/rfc2578#section-7.1.1
  Scenario Outline: Trap processing with different variables types (<variableType> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    When I send <protocol> trap message with trap version 1, OId 1.3.6.1.2.1.34.4.0.2, variable <variableType> and value <variableVal>
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap", series "T" and value "<result>" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | variableType | variableVal | result | protocol |
    | Integer32    | 10          | 10     | UDP      |
    | Counter32    | 12          | 12     | UDP      |
    | TimeTicks    | 13          | 0.13   | UDP      |
    | Integer32    | 10          | 10     | TCP      |
    | Counter32    | 12          | 12     | TCP      |
    | TimeTicks    | 13          | 0.13   | TCP      |

  # Reference: https://tools.ietf.org/html/rfc2578#section-7.1.1
  Scenario Outline: Trap processing with different variables types (v2c supported type) (<variableType> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "1"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    When I send <protocol> trap message with trap version 2c, OId 1.3.6.1.2.1.34.4.0.2, variable <variableType> and value <variableVal>
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap", series "T" and value "<result>" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | variableType | variableVal | result | protocol |
    | Counter64    | 10          | 10     | UDP      |
    | Counter64    | 10          | 10     | TCP      |

  Scenario Outline: Trap processing for OctetString only processes event and alarms and not measurements (version 1 and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol
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
            "oid": "1.3.6.1.2.1.34.4.0.2"
        }]
    }
    """
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version model Id "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    When I send <protocol> trap message with trap version 1, OId 1.3.6.1.2.1.34.4.0.2, variable <variableType> and value <variableVal>
    And I wait for 5 seconds
    Then There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be an alarm with count 1, type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Examples:
    | variableType | variableVal | protocol |
    | OctetString  | 1011        | UDP      |
    | OctetString  | 1011        | TCP      |