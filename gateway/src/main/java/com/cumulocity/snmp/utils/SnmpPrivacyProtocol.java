package com.cumulocity.snmp.utils;

import lombok.Getter;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.smi.OID;

public enum SnmpPrivacyProtocol {
    DES(1), AES128(2), AES192(3), AES256(4);

    @Getter
    private int value;

    SnmpPrivacyProtocol(int value) {
        this.value = value;
    }

    public static OID getPrivacyProtocolOid(int id) {
        OID oid;
        switch (id) {
            case 1:
                oid = PrivDES.ID;
                break;
            case 2:
                oid = PrivAES128.ID;
                break;
            case 3:
                oid = PrivAES192.ID;
                break;
            case 4:
                oid = PrivAES256.ID;
                break;
            default:
                oid = null;
                break;
        }
        return oid;
    }
}
