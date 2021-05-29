/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TK10xCoordinatesTranslatorTest {
    
    @Test
    public void shouldParseLatitude() throws Exception {
        double actual = TK10xCoordinatesTranslator.parseLatitude("5114.3471", "N");
        
        assertThat(actual).isEqualTo(51.2391183);
    }
    
    @Test
    public void shouldParseLongitude() throws Exception {
        double actual = TK10xCoordinatesTranslator.parseLongitude("00643.2373", "E");
        
        assertThat(actual).isEqualTo(6.7206217);
    }

}
