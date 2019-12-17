@autoDiscovery
Feature: Auto-discovery scenarios

  Background:
    Given Scenario tenant is created

  Scenario: Auto-discovery process triggered for UDP device using auto discovery operation
    Given I start and register gateway with polling port 1025 and UDP protocol
    # Wait 65 seconds because the realtime operation subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp UDP simulation device with ip 127.0.0.1 and port 1025
    When I create snmp auto discovery operation on gateway with ip range 127.0.0.1-127.0.0.3
    And I wait until last operation is successful on gateway with timeout 10 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway
    And I stop snmp UDP simulation device

  Scenario: Auto-discovery process triggered for TCP device using auto discovery operation
    Given I start and register gateway with polling port 1025 and TCP protocol
    # Wait 65 seconds because the realtime operation subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp TCP simulation device with ip 127.0.0.1 and port 1025
    When I create snmp auto discovery operation on gateway with ip range 127.0.0.1-127.0.0.3
    And I wait until last operation is successful on gateway with timeout 10 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway
    And I stop snmp TCP simulation device

  Scenario: Auto-discovery process triggered for UDP device with scheduled job
    Given I start and register gateway with polling port 1025 and UDP protocol
    # Wait 65 seconds because the realtime subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp UDP simulation device with ip 127.0.0.1 and port 1025
    When I set snmp gateway configuration with ipRange 127.0.0.1-127.0.0.3 and autoDiscoveryInterval 1
    And I wait for 60 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway
    And I stop snmp UDP simulation device

  Scenario: Auto-discovery process triggered for TCP device with scheduled job
    Given I start and register gateway with polling port 1025 and TCP protocol
    # Wait 65 seconds because the realtime subscription to the gateway device takes one minute
    And I wait for 65 seconds
    And I run snmp TCP simulation device with ip 127.0.0.1 and port 1025
    When I set snmp gateway configuration with ipRange 127.0.0.1-127.0.0.3 and autoDiscoveryInterval 1
    And I wait for 60 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway
    And I stop snmp TCP simulation device