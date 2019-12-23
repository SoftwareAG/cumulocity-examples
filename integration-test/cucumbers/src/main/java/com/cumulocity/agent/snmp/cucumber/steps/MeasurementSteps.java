package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;

import cucumber.api.java.en.Then;

public class MeasurementSteps {

    @Autowired
    protected PlatformProvider platformProvider;

    @Autowired
    protected SnmpDeviceSteps snmpDeviceSteps;

    @Then("^There should be (.+) measurement with type \"(.+)\", fragmentType \"(.+)\" and series \"(.+)\" created for snmp device$")
    public List<MeasurementRepresentation> findDeviceMeasurement(int count, String type, String fragmentType, String series) {
        MeasurementFilter filter = new MeasurementFilter()
                .byType(type)
                .byValueFragmentTypeAndSeries(fragmentType, series)
                .bySource(snmpDeviceSteps.getLastSnmpDevice().getId());
        List<MeasurementRepresentation> measurements = measurementApi().getMeasurementsByFilter(filter).get(2000).getMeasurements();
        assertThat(measurements.size()).isEqualTo(count);
        return measurements;
    }

    @Then("^There should be (.+) measurement with type \"(.+)\", fragmentType \"(.+)\", series \"(.+)\" and value \"(.+)\" created for snmp device$")
    public void findDeviceMeasurement(int count, String type, String fragmentType, String series, String valueStr) {
        List<MeasurementRepresentation> measurements = findDeviceMeasurement(count, type, fragmentType, series);
        measurements.stream().allMatch(measurement -> {
            Map<String, Object> measurementSeries = (Map<String, Object>) measurement.get(fragmentType);
            Map<String, Object> valueMap = (Map<String, Object>) measurementSeries.get(series);
            Object valObj = valueMap.get("value");
            return valueStr.equals(valObj.toString());
        });
    }

    private MeasurementApi measurementApi() {
        return platformProvider.getTestPlatform().getMeasurementApi();
    }
}
