package com.cumulocity.agent.snmp;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty" }, tags = { "not @ignore" })
public class SnmpGatewayCucumberTest {
}
