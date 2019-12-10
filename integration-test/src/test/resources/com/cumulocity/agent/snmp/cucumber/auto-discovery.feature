@autoDiscovery @test
Feature: Auto-discovery scenarios

  Background:
    Given Scenario tenant is created
    And I create gateway configuration
    And There are no existing device credentials available locally
    And I start the gateway process
    And I register the gateway device
    And I accept gateway device request
    Then Gateway device should be created

  Scenario: Auto-discovery process
    Given I run snmp simulation device with udp endpoint 127.0.0.1:1024
    And I create snmp auto discovery operation on gateway with ip range 127.0.0.1-127.0.0.5
    And I wait until last operation is successful on gateway with timeout 5 seconds
    Then There should be a snmp device with ip 127.0.0.1 and port 1024 created as child on gateway
    And I stop the snmp simulation device