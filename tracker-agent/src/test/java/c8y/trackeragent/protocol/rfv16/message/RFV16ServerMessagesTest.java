/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.rfv16.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.utils.message.TrackerMessage;

public class RFV16ServerMessagesTest {
    
    private static final String HHMMSS = "010000";
    
    private final RFV16ServerMessages messages = new RFV16ServerMessages();
    
    @Before
    public void init() {
        DateTime dateTime = RFV16ServerMessages.HHMMSS.parseDateTime(HHMMSS);
        DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());
    }
    
    @Test
    public void createPositioningMonitoringCommand() throws Exception {
        TrackerMessage actual = messages.reportMonitoringCommand("7893267561", "5");
        
        assertThat(actual.asText()).isEqualTo("*HQ,7893267561,D1," + HHMMSS + ",5,1#");
    }

}
