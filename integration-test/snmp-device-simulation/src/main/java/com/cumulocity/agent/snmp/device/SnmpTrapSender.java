package com.cumulocity.agent.snmp.device;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpTrapSender {
	private static final String community = "public"; // SET THIS
	private static final String trapOid = "1.3.6.1.2.1.34.4.0.2";
	private static final String ipAddress = "ec2-18-185-106-102.eu-central-1.compute.amazonaws.com"; // SET THIS (this is the destination address)
	private static final int port = 6671;
	
	private static Random random = new Random();

	public static void main(String args[]) {

		// PICK THE VERSION(S) YOU WANT TO SEND
		
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		long count = 0;
		while (true) {
			sendSnmpV1V2Trap(SnmpConstants.version1);
			// sendSnmpV1V2Trap(SnmpConstants.version2c);
			// sendSnmpV3Trap();

			Date date = new Date(System.currentTimeMillis());
			System.out.println("Trap sent by " + formatter.format(date) + " = " + ++count);
//			System.out.println(++count);
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This methods sends the V1/V2 trap
	 * 
	 * @param version
	 */
	private static void sendSnmpV1V2Trap(int version) {
		sendV1orV2Trap(version, community, ipAddress, port);
	}

	private static PDU createPdu(int snmpVersion) {

		PDU pdu;
		if (snmpVersion == SnmpConstants.version1) {

			PDUv1 pdu1 = new PDUv1();
			pdu1.setType(PDU.V1TRAP);
			pdu1.setEnterprise(new OID("1.3.6.1.2.1.34.4.0.2"));
			pdu1.setAgentAddress(new IpAddress("3.122.112.234")); // SET THIS. This is the sender address
			pdu1.setSpecificTrap(5);
			pdu1.setGenericTrap(23);
			pdu = pdu1;

		} else {
			PDU pdu2 = new PDU();
			pdu2.setType(PDU.TRAP);
			pdu2.setRequestID(new Integer32(123));
			pdu = pdu2;
		}

//		pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
//		pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
//		pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));
		pdu.add(new VariableBinding(new OID(trapOid), new Counter32(random.nextInt(100))));
		return pdu;
	}

	private static void sendV1orV2Trap(int snmpVersion, String community, String ipAddress, int port) {
		try {
			// create v1/v2 PDU
			PDU snmpPDU = createPdu(snmpVersion);

			// Create Transport Mapping
			TransportMapping<?> transport = new DefaultUdpTransportMapping();
			//TransportMapping<?> transport = new DefaultTcpTransportMapping();

			// Create Target
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(community));
			comtarget.setVersion(snmpVersion);
			comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
			//comtarget.setAddress(new TcpAddress(ipAddress + "/" + port));
			comtarget.setRetries(2);
			comtarget.setTimeout(5000);

			// Send the PDU
			Snmp snmp = new Snmp(transport);
			snmp.send(snmpPDU, comtarget);
			//System.out.println("Sent Trap to (IP:Port)=> " + ipAddress + ":" + port);
			snmp.close();
		} catch (Exception e) {
			System.err.println("Error in Sending Trap to (IP:Port)=> " + ipAddress + ":" + port);
			System.err.println("Exception Message = " + e.getMessage());
		}
	}

	/**
	 * Sends the v3 trap
	 */
	private static void sendSnmpV3Trap() {
		try {
			Address targetAddress = GenericAddress.parse("udp:" + ipAddress + "/" + port);
			TransportMapping<?> transport = new DefaultUdpTransportMapping();
			Snmp snmp = new Snmp(transport);

			USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(),
					new OctetString(MPv3.createLocalEngineID()), 0);
			SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES192());
			SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256());
			SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());
			SecurityModels.getInstance().addSecurityModel(usm);

			// transport.listen();

			String username = "username";
			String authpassphrase = "authpassphrase";
			String privacypassphrase = "privacypassphrase";

			snmp.getUSM().addUser( // SET THE USERNAME, PROTOCOLS, PASSPHRASES
					new OctetString(username), new UsmUser(new OctetString(username), AuthMD5.ID,
							new OctetString(authpassphrase), PrivAES128.ID, new OctetString(privacypassphrase)));

			// Create Target
			UserTarget target = new UserTarget();
			target.setAddress(targetAddress);
			target.setRetries(1);
			target.setTimeout(11500);
			target.setVersion(SnmpConstants.version3);
			target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
			target.setSecurityName(new OctetString(username));

			// Create PDU for V3
			ScopedPDU pdu = new ScopedPDU();
			pdu.setType(ScopedPDU.NOTIFICATION);
			pdu.setRequestID(new Integer32(1234));
			pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
			pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));
			pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));

			// Send the PDU
			snmp.send(pdu, target);
			System.out.println("Sending Trap to (IP:Port)=> " + ipAddress + ":" + port);
			snmp.addCommandResponder(new CommandResponder() {
				public void processPdu(CommandResponderEvent arg0) {
					System.out.println(arg0);
				}
			});
			snmp.close();
		} catch (Exception e) {
			System.err.println("Error in Sending Trap to (IP:Port)=> " + ipAddress + ":" + port);
			System.err.println("Exception Message = " + e.getMessage());
		}
	}
}