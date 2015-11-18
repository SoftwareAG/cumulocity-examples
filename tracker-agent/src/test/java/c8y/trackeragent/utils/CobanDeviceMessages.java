package c8y.trackeragent.utils;

import static java.lang.String.format;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;

public class CobanDeviceMessages {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanDeviceMessages.class);
    
    public static byte[] logon(final String imei) throws Exception {
        String msg = format("**,imei:%s,A;", imei);
        logger.info("Message prepared:{}", msg);
        return asBytes(msg);
    }

    private static byte[] asBytes(String msg) {
        try {
            return msg.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static byte[] getPositionReport(final String imei, Position position) throws Exception {
        return null;
    }

}
