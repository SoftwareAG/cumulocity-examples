package com.cumulocity.snmp.utils;

import org.bouncycastle.util.IPAddress;

public class IPAddressUtil {

    private final int value;

    public IPAddressUtil(int value) {
        this.value = value;
    }

    public IPAddressUtil(String stringValue) {
        String[] parts = stringValue.split("\\.");
        if( parts.length != 4 ) {
            throw new IllegalArgumentException();
        }
        value =
                (Integer.parseInt(parts[0], 10) << (8*3)) & 0xFF000000 |
                        (Integer.parseInt(parts[1], 10) << (8*2)) & 0x00FF0000 |
                        (Integer.parseInt(parts[2], 10) << (8*1)) & 0x0000FF00 |
                        (Integer.parseInt(parts[3], 10) << (8*0)) & 0x000000FF;
    }

    public static boolean isValid(String address)
    {
        return IPAddress.isValid(address);
    }

    public int getOctet(int i) {

        if( i<0 || i>=4 ) throw new IndexOutOfBoundsException();

        return (value >> (i*8)) & 0x000000FF;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(int i=3; i>=0; --i) {
            sb.append(getOctet(i));
            if( i!= 0) sb.append(".");
        }

        return sb.toString();

    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof IPAddressUtil) {
            return value==((IPAddressUtil)obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public int getValue() {
        return value;
    }

    public IPAddressUtil next() {
        return new IPAddressUtil(value+1);
    }
}
