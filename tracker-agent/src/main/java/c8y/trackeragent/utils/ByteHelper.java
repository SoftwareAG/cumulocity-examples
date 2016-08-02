package c8y.trackeragent.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteHelper {

    private static final String ASCII = "US-ASCII";

    public static byte[] getBytes(String text) {
        try {
            return text.getBytes(ASCII);
        } catch (UnsupportedEncodingException e) {
            // never happen
            return null;
        }
    }
    
    public static String getString(byte[] bytes) {
        try {
            return new String(bytes, ASCII);
        } catch (UnsupportedEncodingException e) {
            // never happen
            return null;
        }
    }
    
    public static byte[] stripHead(byte[] report, int length) {
        if (report == null) {
            return null;
        } else if (length <= report.length) {
            return Arrays.copyOfRange(report, length, report.length);
        } else {
            return null;
        }
    }


}
