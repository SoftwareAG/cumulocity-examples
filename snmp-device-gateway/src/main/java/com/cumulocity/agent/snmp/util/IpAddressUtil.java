package com.cumulocity.agent.snmp.util;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.UnsignedBytes;

import java.net.InetAddress;

public class IpAddressUtil {
    public static InetAddress forString(String ipAddress) throws IllegalArgumentException {
        return forString(ipAddress, false);
    }

    public static InetAddress forString(String ipAddress, boolean defaultToLoopbackAddress) throws IllegalArgumentException {
        if(ipAddress == null || ipAddress.trim().isEmpty()) {
            if(defaultToLoopbackAddress) {
                return InetAddress.getLoopbackAddress();
            }
            else {
                return null;
            }
        }

        return InetAddresses.forString(ipAddress.trim().split("[/%]", 2)[0].toLowerCase());
    }

    public static String sanitizeIpAddress(String ipAddress) throws IllegalArgumentException {
        return sanitizeIpAddress(ipAddress, false);
    }

    public static String sanitizeIpAddress(String ipAddress, boolean defaultToLoopbackAddress) throws IllegalArgumentException {
        InetAddress inetAddress = forString(ipAddress, defaultToLoopbackAddress);
        if(inetAddress == null) {
            return null;
        }

        return inetAddress.getHostAddress();
    }

    public static int compare(InetAddress firstAddress, InetAddress secondAddress) {
        return UnsignedBytes.lexicographicalComparator().compare(firstAddress.getAddress(), secondAddress.getAddress());
    }
}
