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

package c8y.example.decoders.hex.util;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.text.MessageFormat;

import static org.junit.Assert.*;

public class MeasurementTest {

    @Test
    public void set() {
        final String[] CHECKS = new String[]{
                "\"time\":\"1970-01-01T00:00:00.000Z\"",
                "\"source\":{\"id\":\"12345\"}",
                "\"type\":\"c8y_LoraDemonstratorTelemetry\"",
                "\"c8y_Temperature\":{\"T\":{\"value\":23}}",
                "\"c8y_Battery\":{\"voltage\":{\"value\":3347}}",
                "\"c8y_SignalStrength\":{\"",
                "\"RSSI\":{\"value\":-98}",
                "\"SNR\":{\"value\":15}",
        };
        final String ERROR_FORMAT = "The following message:\n{0}\n was not found in:\n{1}";


        Measurement m = new Measurement();
        m.setSource(ManagedObjects.asManagedObject(new GId("12345")));
        m.setType("c8y_LoraDemonstratorTelemetry");
        m.setDateTime(new DateTime(0).toDateTime(DateTimeZone.UTC)); //1970-01-01T00:00:00.000Z
        m.set("c8y_Temperature.T.value", 23);
        m.set("c8y_Battery.voltage.value", 3347);
        m.set("c8y_SignalStrength.RSSI.value", -98);
        m.set("c8y_SignalStrength.SNR.value", 15);

        String json = m.toJSON();
        for (String check: CHECKS)
            assertTrue(MessageFormat.format(ERROR_FORMAT, check, json), json.contains(check));
    }
}