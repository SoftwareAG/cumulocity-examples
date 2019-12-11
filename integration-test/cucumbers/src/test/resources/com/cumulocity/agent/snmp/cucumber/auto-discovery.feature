@autoDiscovery @test
Feature: Auto-discovery scenarios

  Background:
    Given Scenario tenant is created
    And I create gateway configuration with polling port 1025
    And There are no existing device credentials available locally
    And I start the gateway process
    And I register the gateway device
    And I accept gateway device request
    Then Gateway device should be created

  Scenario: Auto-discovery process
    # Wait 65 seconds because the operation subscription to the gateway devices takes one minute
    Then I wait for 65 seconds
    Given I run snmp simulation device with ip 127.0.0.1 and port 1025
    And I create snmp auto discovery operation on gateway with ip range 127.0.0.1-127.0.0.3
    And I wait until last operation is successful on gateway with timeout 10 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1025 created as child on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.2 on gateway
    And There should be an auto discovery alarm created for ip 127.0.0.3 on gateway