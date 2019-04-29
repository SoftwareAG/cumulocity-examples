package com.cumulocity.snmp.utils;

import lombok.Getter;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.smi.OID;

public enum SnmpAuthProtocol {
    MD5(1, "MD5DES", AuthMD5.ID), SHA(2, "SHADES", AuthSHA.ID);

    @Getter
    private int value;

    @Getter
    private String name;

    @Getter
    private OID oId;

    SnmpAuthProtocol(int value, String name, OID oId) {
        this.value = value;
        this.name = name;
        this.oId = oId;
    }

    public static String getAuthProtocolName(int id) {
        String name;
        switch (id) {
            case 1:
                name = "MD5DES";
                break;
            case 2:
                name = "SHADES";
                break;
            default:
                name = "";
                break;
        }
        return name;
    }

    public static OID getAuthProtocolOid(int id) {
        OID oid;
        switch (id) {
            case 1:
                oid = AuthMD5.ID;
                break;
            case 2:
                oid = AuthSHA.ID;
                break;
            default:
                oid = null;
                break;
        }
        return oid;
    }
}
