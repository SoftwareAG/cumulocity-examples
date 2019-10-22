/*
 * Copyright © 2019 Software AG, Darmstadt, Germany and/or its licensors
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package c8y.example.decoders.json;

import c8y.example.decoders.json.util.Measurement;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.microservice.customdecoders.api.service.DecoderService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Getter
@Setter
@Slf4j
public class JsonDecoderService implements DecoderService {

    public final static HashMap<String, String> mapping = new HashMap<>();
    static {
        mapping.put("totalActiveEnergy",            "c8y_TotalEnergy.Active.value");
        mapping.put("totalForwardActiveEnergy",     "c8y_TotalEnergy.forwardActive.value");
        mapping.put("totalReverseActiveEnergy",     "c8y_TotalEnergy.reverseActive.value");
        mapping.put("totalForwardReactiveEnergy",   "c8y_TotalEnergy.forwardReactive.value");
        mapping.put("totalReverseReactiveEnergy",   "c8y_TotalEnergy.reverseReactive.value");
        mapping.put("forwardActiveEnergyT1",        "c8y_ForwardActiveEnergy.T1.value");
        mapping.put("forwardActiveEnergyT2",        "c8y_ForwardActiveEnergy.T2.value");
        mapping.put("forwardActiveEnergyT3",        "c8y_ForwardActiveEnergy.T3.value");
        mapping.put("forwardActiveEnergyT4",        "c8y_ForwardActiveEnergy.T4.value");
        mapping.put("voltagePhaseA",                "c8y_Voltage.phaseA.value");
        mapping.put("voltagePhaseB",                "c8y_Voltage.phaseB.value");
        mapping.put("voltagePhaseC",                "c8y_Voltage.phaseC.value");
        mapping.put("currentPhaseA",                "c8y_Current.phaseA.value");
        mapping.put("currentPhaseB",                "c8y_Current.phaseB.value");
        mapping.put("currentPhaseC",                "c8y_Current.phaseC.value");
        mapping.put("totalActivePower",             "c8y_ActivePower.Total.value");
        mapping.put("activePowerPhaseA",            "c8y_ActivePower.phaseA.value");
        mapping.put("activePowerPhaseB",            "c8y_ActivePower.phaseB.value");
        mapping.put("activePowerPhaseC",            "c8y_ActivePower.phaseC.value");
        mapping.put("totalReactivePower",           "c8y_ReactivePower.total.value");
        mapping.put("reactivePowerPhaseA",          "c8y_ReactivePower.phaseA.value");
        mapping.put("reactivePowerPhaseB",          "c8y_ReactivePower.phaseB.value");
        mapping.put("reactivePowerPhaseC",          "c8y_ReactivePower.phaseC.value");
        mapping.put("totalApparentPower",           "c8y_ApparentPower.Total.value");
        mapping.put("apparentPowerPhaseA",          "c8y_ApparentPower.phaseA.value");
        mapping.put("apparentPowerPhaseB",          "c8y_ApparentPower.phaseB.value");
        mapping.put("apparentPowerPhaseC",          "c8y_ApparentPower.phaseC.value");
        mapping.put("totalPowerFactor",             "c8y_PowerFactor.total.value");
        mapping.put("powerFactorPhaseA",            "c8y_PowerFactor.phaseA.value");
        mapping.put("powerFactorPhaseB",            "c8y_PowerFactor.phaseB.value");
        mapping.put("powerFactorPhaseC",            "c8y_PowerFactor.phaseC.value");
        mapping.put("frequency",                    "c8y_PowerLine.frequency.value");
    }


    private MeasurementApi measurementApi;

    @Autowired
    public JsonDecoderService(MeasurementApi measurementApi) {
        this.measurementApi = measurementApi;
    }

    @Override
    public DecoderResult decode(String payloadToDecode, GId sourceDeviceId, Map<String, String> inputArguments) {

        log.debug("Decoding payload {}.", payloadToDecode);
        DecoderResult decoderResult = new DecoderResult();

        JSONObject input;
        try {
            input = (JSONObject)new JSONParser().parse(payloadToDecode);
        } catch (ParseException p) {
            return decoderResult.setAsFailed(p.getMessage());
        }

        Measurement m = new Measurement();
        m.setSource(ManagedObjects.asManagedObject(sourceDeviceId));
        m.setType("c8y_PowerMeterTelemetry");
        m.setDateTime(new DateTime( (long) input.get("ts")));
        JSONObject values = (JSONObject) input.get("values");
        for(Map.Entry<String, String> entry: mapping.entrySet())
            m.set(entry.getValue(), values.get(entry.getKey()));
        measurementApi.create(m);

        log.debug("Finished decoding byte values");
        return decoderResult;
    }
}
