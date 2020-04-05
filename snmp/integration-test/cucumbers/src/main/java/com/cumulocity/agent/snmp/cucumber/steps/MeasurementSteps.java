/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.agent.snmp.cucumber.steps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.agent.snmp.cucumber.tools.CompareUtil;
import com.cumulocity.agent.snmp.cucumber.tools.AssertionType;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;

import cucumber.api.java.en.Then;

public class MeasurementSteps {

    @Autowired
    protected PlatformProvider platformProvider;

    @Autowired
    protected SnmpDeviceSteps snmpDeviceSteps;

    @Then("^There should be ([0-9]+) measurement with type \"(.+)\", fragmentType \"(.+)\" and series \"(.+)\" created for snmp device$")
    public List<MeasurementRepresentation> findDeviceMeasurement(int count, String type, String fragmentType, String series) {
        List<MeasurementRepresentation> measurements = getMeasurements(type, fragmentType, series);
        CompareUtil.checkExistence(count, measurements.size(), AssertionType.EXACT);
        return measurements;
    }

    @Then("^There should be ([0-9]+) measurement with type \"(.+)\", fragmentType \"(.+)\", series \"(.+)\" and value \"(.+)\" created for snmp device$")
    public void findDeviceMeasurement(int count, String type, String fragmentType, String series, String valueStr) {
        List<MeasurementRepresentation> measurements = getMeasurements(type, fragmentType, series);
        measurements = filterMeasurementsWithValue(measurements, fragmentType, series, valueStr);
        CompareUtil.checkExistence(count, measurements.size(), AssertionType.EXACT);
    }



  @Then("^There should be ([0-9]+)\\-([0-9]+) measurement with type \"(.+)\", fragmentType \"(.+)\" and series \"(.+)\" created for snmp device$")
    public void findDeviceMeasurement(int min, int max, String type, String fragmentType, String series) {
        List<MeasurementRepresentation> measurements = getMeasurements(type, fragmentType, series);
        CompareUtil.checkExistence(min, measurements.size(), AssertionType.AT_LEAST);
        CompareUtil.checkExistence(max, measurements.size(), AssertionType.AT_MAX);
    }

    @Then("^There should be ([0-9]+)\\-([0-9]+) measurement with type \"(.+)\", fragmentType \"(.+)\", series \"(.+)\" and value \"(.+)\" created for snmp device$")
    public void findDeviceMeasurement(int min, int max, String type, String fragmentType, String series, String valueStr) {
        List<MeasurementRepresentation> measurements = getMeasurements(type, fragmentType, series);
        measurements = filterMeasurementsWithValue(measurements, fragmentType, series, valueStr);
        CompareUtil.checkExistence(min, measurements.size(), AssertionType.AT_LEAST);
        CompareUtil.checkExistence(max, measurements.size(), AssertionType.AT_MAX);
    }

    private List<MeasurementRepresentation> getMeasurements(String type, String fragmentType, String series) {
        MeasurementFilter filter = new MeasurementFilter()
                .byType(type)
                .byValueFragmentTypeAndSeries(fragmentType, series)
                .bySource(snmpDeviceSteps.getLastSnmpDevice().getId());
        return measurementApi().getMeasurementsByFilter(filter).get(2000).getMeasurements();
    }

    private List<MeasurementRepresentation> filterMeasurementsWithValue(List<MeasurementRepresentation> measurements, String fragmentType, String series, String valueStr) {
        return measurements.stream()
                .filter(measurement -> {
                    Map<String, Object> measurementSeries = (Map<String, Object>) measurement.get(fragmentType);
                    Map<String, Object> valueMap = (Map<String, Object>) measurementSeries.get(series);
                    Object valObj = valueMap.get("value");
                    return valueStr.equals(valObj.toString());
                })
                .collect(Collectors.toList());
        
    }

    private MeasurementApi measurementApi() {
        return platformProvider.getTestPlatform().getMeasurementApi();
    }
}
