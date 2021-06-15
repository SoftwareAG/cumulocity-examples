/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.rfv16.parser;

import java.util.Collection;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AlarmTypeDecoderTest {
    
    @Test
    public void shouldParseAlarmStatus() throws Exception {
        doShouldParseAlarmStatus("FFFFFFFF");
        doShouldParseAlarmStatus("00000000", RFV16AlarmType.values());
        doShouldParseAlarmStatus("FFFFFFFD", RFV16AlarmType.SOS);
        doShouldParseAlarmStatus("FFFFFFFB", RFV16AlarmType.OVERSPEED);
        doShouldParseAlarmStatus("FFFDFFFF", RFV16AlarmType.LOW_BATTERY);
    }
    
    private void doShouldParseAlarmStatus(String status, RFV16AlarmType... expectedAlarmTypes) {
        Collection<RFV16AlarmType> actual = AlarmTypeDecoder.getAlarmTypes(status);
        assertThat(actual).containsOnly(expectedAlarmTypes);
    }

}
