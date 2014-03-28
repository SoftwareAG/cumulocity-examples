package c8y.trackeragent.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import c8y.Position;

public class Positions {
    
    public final static Position ZERO = aPosition(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    public final static Position SAMPLE_1 = aPosition(new BigDecimal("48.0332"), new BigDecimal("11.5864"), new BigDecimal("599"));

    public static Position aPosition(BigDecimal lat, BigDecimal lng, BigDecimal alt) {
        Position position = new Position();
        position.setLat(lat);
        position.setLng(lng);
        position.setAlt(alt);
        return position;
    }

    public static void assertEqual(Position pos1, Position pos2) {
        assertEqualPosProperty(pos1.getLng(), pos2.getLng());
        assertEqualPosProperty(pos1.getLat(), pos2.getLat());
        assertEqualPosProperty(pos1.getAlt(), pos2.getAlt());
    }

    private static void assertEqualPosProperty(BigDecimal val1, BigDecimal val2) {
        assertEquals(val1.doubleValue(), val2.doubleValue(), 0.01);
    }
}