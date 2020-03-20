package com.cumulocity.agent.snmp.cucumber.steps;

import cucumber.api.java.en.When;

public class GeneralSteps {

    @When("^I wait for (.+) seconds$")
    public void startMiloServer(int seconds) throws Exception {
        Thread.sleep(1000 * seconds);
    }
}
