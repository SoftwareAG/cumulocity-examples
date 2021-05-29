/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

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
    
    public static byte[] stripHead(byte[] report, int headLength) {
        if (report == null) {
            return null;
        } else if (headLength <= report.length) {
            return Arrays.copyOfRange(report, headLength, report.length);
        } else {
            return null;
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b: data)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

    // credits: https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] getHexBytes(String hexString){
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }


}
