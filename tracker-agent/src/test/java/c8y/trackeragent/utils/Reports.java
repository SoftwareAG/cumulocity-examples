package c8y.trackeragent.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import c8y.Position;

public class Reports {
    
    public static final String HEADER = "0000123456|262|02|003002016";

    public static byte[] getTelicReportBytes(String imei, Position position) throws UnsupportedEncodingException {
        String location = sampleTelicReportStr(imei, position);
        byte[] HEADERBYTES = HEADER.getBytes("US-ASCII");
        byte[] LOCATIONBYTES = location.getBytes("US-ASCII");
        byte[] bytes = new byte[HEADERBYTES.length + 5 + LOCATIONBYTES.length + 1];

        int i = 0;
        for (byte b : HEADERBYTES) {
            bytes[i++] = b;
        }
        i += 5;
        for (byte b : LOCATIONBYTES) {
            bytes[i++] = b;
        }
        return bytes;
    }
    
    private static String sampleTelicReportStr(String imei, Position position) {
        return "0721" + 
                imei + 
                "99,200311121210,0,200311121210," +
                asTelicStringCoord(position.getLng()) + 
                "," +
                asTelicStringCoord(position.getLat()) + 
                ",3,4,67,4,,," + 
                asTelicStringCoord(position.getAlt()) +
                ",11032,,010 1,00,238,0,0,0";
    }
    
    private static String asTelicStringCoord(BigDecimal coord) {
        return coord.toString().replaceAll("\\.", "");
    }
}
