package com.cumulocity.agent.snmp.util;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.smi.OID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpUtil {

	public static boolean isValidSnmpVersion(int snmpVersion) {
		return snmpVersion == SnmpConstants.version1  || 
			   snmpVersion == SnmpConstants.version2c || 
			   snmpVersion == SnmpConstants.version3;
	}

	public static boolean isValidAuthProtocol(int authenticationProtocol) {
		return (authenticationProtocol == 1 || authenticationProtocol == 2);
	}

	public static boolean isValidPrivacyProtocol(int privacyProtocol) {
		return (privacyProtocol > 0 && privacyProtocol < 5);
	}

	public static OID getAuthProtocolOid(int id) {
		switch (id) {
			case 1:
				return AuthMD5.ID;
			case 2:
				return AuthSHA.ID;
			default:
				log.error("Unsupported {} authentication protocol selected. Supported protocols are "
						+ "usmHMACMD5AuthProtocol as MD5 and usmHMACSHAAuthProtocol as SHA", id);
				return null;
		}
	}

	public static OID getPrivacyProtocolOid(int id) {
		switch (id) {
			case 1:
				return PrivDES.ID;
			case 2:
				return PrivAES128.ID;
			case 3:
				return PrivAES192.ID;
			case 4:
				return PrivAES256.ID;
			default:
				log.error("Unsupported {} privacy protocol id found. Supported ones are "
						+ "1 for DES, 2 for AES128, 3 for AES192 and 4 for AES256", id);
				return null;
		}
	}
}
