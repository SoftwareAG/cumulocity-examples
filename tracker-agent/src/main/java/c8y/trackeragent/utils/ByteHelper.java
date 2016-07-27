package c8y.trackeragent.utils;

import java.io.UnsupportedEncodingException;

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

}
