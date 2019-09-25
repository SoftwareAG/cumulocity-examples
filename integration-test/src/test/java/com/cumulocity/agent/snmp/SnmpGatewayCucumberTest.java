package com.cumulocity.agent.snmp;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty" }, tags = { "not @ignore" })
public class SnmpGatewayCucumberTest {
}
