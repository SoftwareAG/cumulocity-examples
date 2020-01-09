@trapProcessing
Feature: Trap-processing scenarios

  Background:
    Given Scenario tenant is created

  Scenario Outline: Processing trap with version 1 (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling version model Id 0
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
    Given I start and register gateway with <protocol> protocol and polling version model Id 1
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
    Given I start and register gateway with <protocol> protocol and polling version model Id 3
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
        | username | privPassword      | authPassword   | authProtocol | privProtocol | securityLevel | engineId         |
        | username | privacypassphrase | authpassphrase | 1            | 4            | 3             | 49:U9:39:900:FJ8 |
    And I add last snmp device as child device to the gateway
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

  Scenario Outline: Trap processing without given OID (<protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling version model Id 0
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
    Given I start and register gateway with <protocol> protocol and polling version model Id 0
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

  # Reference: https://tools.ietf.org/html/rfc2578#section-7.1.1
  Scenario Outline: Trap processing with different variables types (<variableType> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling version model Id 0
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
    | OctetString  | 1011        | 1011   | UDP      |
    | Counter32    | 12          | 12     | UDP      |
    | TimeTicks    | 13          | 0.13   | UDP      |
    | Integer32    | 10          | 10     | TCP      |
    | OctetString  | 1011        | 1011   | TCP      |
    | Counter32    | 12          | 12     | TCP      |
    | TimeTicks    | 13          | 0.13   | TCP      |

  # Reference: https://tools.ietf.org/html/rfc2578#section-7.1.1
  Scenario Outline: Trap processing with different variables types (v2c supported type) (<variableType> and <protocol> protocol)
    Given I start and register gateway with <protocol> protocol and polling version model Id 1
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
    Given I start and register gateway with <protocol> protocol and polling version model Id 0
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