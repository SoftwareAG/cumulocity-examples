/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.telic.parser;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

public class PositionParser {

    private static final int MINIMUM_LATITUDE_IN_EXT_MODE = 8;
    private static final int MINIMUM_LONGITUDE_IN_EXT_MODE = 9;
    private static final BigDecimal STANDARD_MODE_DIVISOR = new BigDecimal(10000);
    private static final BigDecimal EXTENDED_MODE_DIVISOR = new BigDecimal(1000000);
    
    public static final PositionParser LATITUDE_PARSER = new PositionParser(MINIMUM_LATITUDE_IN_EXT_MODE);
    public static final PositionParser LONGITUDE_PARSER = new PositionParser(MINIMUM_LONGITUDE_IN_EXT_MODE);

    private final int minumumLegthInExtMode;

    private PositionParser(int minumumLegthInExtMode) {
        this.minumumLegthInExtMode = minumumLegthInExtMode;
    }
    
    public BigDecimal parse(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }
        input = input.trim();
        BigDecimal val = new BigDecimal(input);
        boolean extendedMode = input.length() >= minumumLegthInExtMode;
        BigDecimal divisior = extendedMode ? EXTENDED_MODE_DIVISOR : STANDARD_MODE_DIVISOR;
        return val.divide(divisior);
    }

}
