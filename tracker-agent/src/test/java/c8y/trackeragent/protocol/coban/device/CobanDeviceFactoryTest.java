/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.device;

import static c8y.trackeragent.protocol.coban.device.CobanDeviceFactory.formatLocationReportInterval;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CobanDeviceFactoryTest {
    
    @Test
    public void shouldFormatTimeIntervalInSeconds() throws Exception {
        assertThat(formatLocationReportInterval(5)).isEqualTo("05s");
        assertThat(formatLocationReportInterval(60)).isEqualTo("01m");
        assertThat(formatLocationReportInterval(90)).isEqualTo("01m");
        assertThat(formatLocationReportInterval(180)).isEqualTo("03m");
    }

}
