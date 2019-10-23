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

package c8y.example.decoders.hex;

import c8y.Position;
import c8y.example.decoders.hex.util.Measurement;
import c8y.example.decoders.hex.util.Message;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.microservice.customdecoders.api.service.DecoderService;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;


@Component
@Slf4j
public class HexDecoderService implements DecoderService {


    private final MeasurementApi measurementApi;
    private final InventoryApi inventoryApi;

    @Autowired
    public HexDecoderService(MeasurementApi measurementApi, InventoryApi inventoryApi) {
        this.measurementApi = measurementApi;
        this.inventoryApi = inventoryApi;
    }

    @Override
    public DecoderResult decode(String payloadToDecode, GId sourceDeviceId, Map<String, String> inputArguments) {

        log.debug("Decoding payload {}.", payloadToDecode);
        DecoderResult decoderResult = new DecoderResult();
        Message msg;
        try {
            msg = new Message(payloadToDecode);
        } catch (IllegalArgumentException e) {
            return decoderResult.setAsFailed(e.getMessage());
        }

        if (msg.isAccelerometerTriggered())
            decoderResult.addAlarm(accelerometerAlarm(sourceDeviceId), false);

        if (msg.isButtonPressed())
            decoderResult.addEvent(buttonEvent(sourceDeviceId), false);

        Position pos = msg.getPosition();
        decoderResult.addEvent(locationUpdateEvent(sourceDeviceId, pos), false);
        inventoryApi.update(positionUpdate(sourceDeviceId, pos));

        measurementApi.create(createMeasurement(sourceDeviceId, msg));

        log.debug("Finished decoding byte values");
        return decoderResult;

    }

    private AlarmRepresentation accelerometerAlarm(GId sourceId){
        AlarmRepresentation accelerometerAlarm = new AlarmRepresentation();
        accelerometerAlarm.setSource(ManagedObjects.asManagedObject(sourceId));
        accelerometerAlarm.setType("c8y_AccelerometerAlarm");
        accelerometerAlarm.setText("Transmission was triggered by accelerometer");
        accelerometerAlarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        accelerometerAlarm.setDateTime(new DateTime());
        return accelerometerAlarm;
    }

    private EventRepresentation buttonEvent(GId sourceId) {
        EventRepresentation buttonEvent = new EventRepresentation();
        buttonEvent.setSource(ManagedObjects.asManagedObject(sourceId));
        buttonEvent.setType("c8y_ButtonEvent");
        buttonEvent.setText("Transmission was triggered by button press");
        buttonEvent.setDateTime(new DateTime());
        return buttonEvent;
    }

    private EventRepresentation locationUpdateEvent(GId sourceId, Position position) {
        EventRepresentation locationUpdateEvent = new EventRepresentation();
        locationUpdateEvent.setSource(ManagedObjects.asManagedObject(sourceId));
        locationUpdateEvent.setType("c8y_LocationUpdate");
        locationUpdateEvent.setText("Location updated");
        locationUpdateEvent.setDateTime(new DateTime());
        locationUpdateEvent.set(position);
        return locationUpdateEvent;
    }

    private ManagedObjectRepresentation positionUpdate(GId sourceId, Position position) {
        ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
        mo.setId(sourceId);
        mo.set(position);
        return mo;
    }

    private Measurement createMeasurement(GId sourceId, Message msg) {
        Measurement m = new Measurement();
        m.setSource(ManagedObjects.asManagedObject(sourceId));
        m.setType("c8y_LoraDemonstratorTelemetry");
        m.setDateTime(new DateTime()); //1970-01-01T01:00:00.000+01:00
        m.set("c8y_Temperature.T.value", msg.getTemperature());
        m.set("c8y_Battery.voltage.value", msg.getBatteryVoltage());
        m.set("c8y_SignalStrength.RSSI.value", msg.getRSSI());
        m.set("c8y_SignalStrength.SNR.value", msg.getSNR());
        return m;
    }

}
