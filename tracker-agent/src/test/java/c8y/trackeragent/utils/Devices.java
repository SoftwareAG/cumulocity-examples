/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Devices {

    private static Logger logger = LoggerFactory.getLogger(Devices.class);

    public static final int IMEI_LENGTH = 4;
    public static final String IMEI_1 = "187182";
    public static final String IMEI_2 = "012345";

    public static String randomImei() {
        String imei = "TT" + RandomStringUtils.random(IMEI_LENGTH, true, true);
        logger.info("Random imei: {} ", imei);
        return imei;
    }

}
