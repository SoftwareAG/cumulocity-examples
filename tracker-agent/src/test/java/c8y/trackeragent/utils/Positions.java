/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import c8y.Position;

public class Positions {
    
    public final static Position ZERO = aPosition(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

    public final static Position SAMPLE_1 = aPosition(new BigDecimal("48.0332"), new BigDecimal("11.5864"), new BigDecimal("599"));
    public final static Position SAMPLE_2 = aPosition(new BigDecimal("47.0332"), new BigDecimal("10.5864"), new BigDecimal("598"));
    public final static Position SAMPLE_3 = aPosition(new BigDecimal("46.0332"), new BigDecimal("9.5864"), new BigDecimal("597"));
    public final static Position SAMPLE_4 = aPosition(new BigDecimal("45.0332"), new BigDecimal("8.5864"), new BigDecimal("596"));
    public final static Position SAMPLE_5 = aPosition(new BigDecimal("48.0332"), new BigDecimal("11.5864"), new BigDecimal("599"), 4857L);
    public final static Position TK10xSample = aPosition(new BigDecimal("5114.3471"), new BigDecimal("00643.2373"), BigDecimal.ZERO);
    
    public static Position aPosition(BigDecimal lat, BigDecimal lng, BigDecimal alt) {
        Position position = new Position();
        position.setLat(lat);
        position.setLng(lng);
        position.setAlt(alt);
        return position;
    }

    public static Position aPosition(BigDecimal lat, BigDecimal lng, BigDecimal alt, Long accuracy) {
        Position position = new Position();
        position.setLat(lat);
        position.setLng(lng);
        position.setAlt(alt);
        position.setAccuracy(accuracy);
        return position;
    }

    public static void assertEqual(Position pos1, Position pos2) {
        assertEqualPosProperty(pos1.getLng(), pos2.getLng());
        assertEqualPosProperty(pos1.getLat(), pos2.getLat());
        assertEqualPosProperty(pos1.getAlt(), pos2.getAlt());
    }
    
    public static Position random() {
        Position[] positions = new Position[] {SAMPLE_1, SAMPLE_2, SAMPLE_3, SAMPLE_4};
        int index = (int) (System.currentTimeMillis() % positions.length);
        return positions[index];
    }

    private static void assertEqualPosProperty(BigDecimal val1, BigDecimal val2) {
        assertEquals(val1.doubleValue(), val2.doubleValue(), 0.01);
    }
    
    
}