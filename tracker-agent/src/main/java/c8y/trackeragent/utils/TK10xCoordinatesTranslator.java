/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import c8y.Position;

public class TK10xCoordinatesTranslator {

    private static final String HEMISPHERE_WEST = "W";
    private static final String HEMISPHERE_SOUTH = "S";
    private static final double MAX_ARG = 99999.0;
    private static final double INVALID_LONG = 180.0;
    private static final double INVALID_LAT = 90.0;
    private static final double ACCURACY = Math.pow(10, 7);

    /**
     * @param latitudeStr
     *            expected: DDmm.mmmm
     * @param hemisphereSymbol
     *            expected "N" or "S" (-1)
     */
    public static double parseLatitude(String latitudeStr, String hemisphereSymbol) {
        ParsedDouble parsedDouble = parseDouble(latitudeStr);
        if (!parsedDouble.isValid()) {
            return INVALID_LAT;
        }
        double latitude = calculate(parsedDouble.getValue());
        return HEMISPHERE_SOUTH.equals(hemisphereSymbol) ? -latitude : latitude;
    }

    /**
     * @param longitudeStr
     *            expected: DDDmm.mmmm
     * @param hemisphereSymbol
     *            expected: "E" or "W" (-1)
     */
    public static double parseLongitude(String longitudeStr, String hemisphereSymbol) {
        ParsedDouble parsedDouble = parseDouble(longitudeStr);
        if (!parsedDouble.isValid()) {
            return INVALID_LONG;
        }
        double longitude = calculate(parsedDouble.getValue());
        return HEMISPHERE_WEST.equals(hemisphereSymbol) ? -longitude : longitude;
    }

    public static Position parse(Position arg) {
        Position result = new Position();
        result.setLat(valueOf(parseLatitude(arg.getLat().toString(), null)));
        result.setLng(valueOf(parseLongitude(arg.getLng().toString(), null)));
        result.setAlt(BigDecimal.ZERO);
        return result;
    }

    private static ParsedDouble parseDouble(String val) {
        if (StringUtils.isEmpty(val)) {
            return new ParsedDouble();
        }
        try {
            double result = Double.parseDouble(val);
            return new ParsedDouble(result);
        } catch (NumberFormatException nfe) {
            return new ParsedDouble();
        }
    }

    private static double calculate(double arg) {
        double minutes = (double) ((long) arg / 100L);
        double degrees = arg - (minutes * 100.0);
        return round(degrees / 60.0) + minutes;
    }

    private static double round(double val) {
        return Math.round(val * ACCURACY) / ACCURACY;
    }

    private static class ParsedDouble {
        private final boolean valid;
        private final double value;

        public ParsedDouble() {
            this.valid = false;
            this.value = -1;
        }

        public ParsedDouble(double value) {
            this.valid = true;
            this.value = value;
        }

        public boolean isValid() {
            return valid && value < MAX_ARG;
        }

        public double getValue() {
            return value;
        }
    }
}
