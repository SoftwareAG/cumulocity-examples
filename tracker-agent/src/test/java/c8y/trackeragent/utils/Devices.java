package c8y.trackeragent.utils;

import org.apache.commons.lang.RandomStringUtils;

public class Devices {
    
    public static final int IMEI_LENGTH = 6; 
    public static final String IMEI_1 = "187182";
    public static final String IMEI_2 = "012345";
    
    public static String randomImei() {
        return RandomStringUtils.random(IMEI_LENGTH, true, true);
    }

}
