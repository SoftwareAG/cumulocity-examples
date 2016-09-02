package c8y.trackeragent.utils;

import org.apache.commons.lang.RandomStringUtils;
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
