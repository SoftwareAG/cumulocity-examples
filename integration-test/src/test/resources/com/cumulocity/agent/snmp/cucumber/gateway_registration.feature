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