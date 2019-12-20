@trapProcessing
Feature: Trap-processing scenarios

  Background:
    Given Scenario tenant is created

  Scenario: Processing trap with UDP (version 1)
    Given I start and register gateway with UDP protocol and polling version model Id 0
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
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send UDP trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be 1 alarm with type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Scenario: Processing trap with UDP (version 2c)
    Given I start and register gateway with UDP protocol and polling version model Id 1
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
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version "1"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send UDP trap message with trap version 2c and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be 1 alarm with type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Scenario: Processing trap with UDP (version 3)
    Given I start and register gateway with UDP protocol and polling version model Id 3
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
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025", version "3" and authentication:
        | username | privPassword      | authPassword   | authProtocol | privProtocol | securityLevel | engineId         |
        | username | privacypassphrase | authpassphrase | 1            | 4            | 3             | 49:U9:39:900:FJ8 |
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send UDP trap message with trap version 3 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 1 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be 1 alarm with type "c8y_Trap", text "Trap alarm created" and severity "WARNING" created for snmp device

  Scenario: Trap processing without given OID
    Given I start and register gateway with UDP protocol and polling version model Id 0
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
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version "0"
    And I add last snmp device as child device to the gateway
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send UDP trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.3
    And I wait for 5 seconds
    Then There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 0 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be 0 alarm with type "c8y_Trap" and text "Trap alarm created" created for snmp device

  Scenario: Alarm creation if the device is unknown to gateway and do not process trap
    Given I start and register gateway with UDP protocol and polling version model Id 0
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
    And I create a snmp device with device protocol "Snmp device protocol", ip "127.0.0.1", port "1025" and version "0"
    # Wait 65 seconds because refresh gateway objects job is triggered in 1 minute
    And I wait for 65 seconds
    And I send UDP trap message with trap version 1 and OId 1.3.6.1.2.1.34.4.0.2
    And I wait for 5 seconds
    Then There should be 1 alarm with type "c8y_TRAPReceivedFromUnknownDevice-", text "Trap received from an unknown device with IP address : 127.0.0.1" and severity "MAJOR" created for gateway
    And There should be 0 measurement with type "c8y_Trap", fragmentType "c8y_Trap" and series "T" created for snmp device
    And There should be 0 event with type "c8y_Trap" and text "Trap event created" created for snmp device
    And There should be 0 alarm with type "c8y_Trap" and text "Trap alarm created" created for snmp device