/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.mt90g.parser;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

import org.junit.Test;

public class MT90GParserTest {

    
    @Test
    public void shouldCorrectlyCalculateBattery() throws Exception {
        MT90GParser mt90gParser = new MT90GParser(null,null);
        
        BigDecimal batteryVoltage = mt90gParser.getBattery("00D2|0000|0000|09CA|0002");
        
        assertThat(batteryVoltage).isEqualTo(new BigDecimal("4.03"));
    }
}
