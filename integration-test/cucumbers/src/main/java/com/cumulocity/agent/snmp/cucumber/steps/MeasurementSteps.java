package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;
import com.google.common.collect.Iterables;

import cucumber.api.java.en.Then;

public class MeasurementSteps {

    @Autowired
    protected PlatformProvider platformProvider;

    @Autowired
    protected SnmpDeviceSteps snmpDeviceSteps;

    @Then("^There should be (.+) measurement with type \"(.+)\", fragmentType \"(.+)\" and series \"(.+)\" created for snmp device$")
    public void findMeasurementWithType(int count, String type, String fragmentType, String series) {
        MeasurementFilter filter = new MeasurementFilter()
                .byType(type)
                .byValueFragmentTypeAndSeries(fragmentType, series)
                .bySource(snmpDeviceSteps.getLastSnmpDevice().getId());
        Iterable<MeasurementRepresentation> measurements = measurementApi().getMeasurementsByFilter(filter).get().allPages();
        assertThat(Iterables.size(measurements)).isEqualTo(count);
    }

    private MeasurementApi measurementApi() {
        return platformProvider.getTestPlatform().getMeasurementApi();
    }
}
