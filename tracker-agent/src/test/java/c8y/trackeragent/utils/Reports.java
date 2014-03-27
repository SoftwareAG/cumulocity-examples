package c8y.trackeragent.utils;

import java.io.UnsupportedEncodingException;

public class Reports {
    
    public static final String HEADER = "0000123456|262|02|003002016";
    public static final String REPORTSTR = "0721%s99,200311121210,0,200311121210,115864,480332,3,4,67,4,,,599,11032,,010 1,00,238,0,0,0";

    public static byte[] getTelicReportBytes(String imei) throws UnsupportedEncodingException {
        String location = String.format(REPORTSTR, imei);
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
}
