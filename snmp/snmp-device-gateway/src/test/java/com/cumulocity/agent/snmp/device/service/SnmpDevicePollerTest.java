/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.agent.snmp.device.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.UsmUser;
import org.snmp4j.security.UsmUserEntry;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.test.util.ReflectionTestUtils;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.DeviceAuthentication;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.SnmpDeviceProperties;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(MockitoJUnitRunner.class)
public class SnmpDevicePollerTest {

	@Mock
	private GatewayProperties.SnmpProperties gatewaySnmpProperties;

	@Mock
	SnmpDeviceProperties snmpProperties;

	@Mock
	private DeviceManagedObjectWrapper deviceWrapper;

	@Mock
	private List<VariableBinding> variableBindingList;

	private Logger logger;

	private ListAppender<ILoggingEvent> listAppender;

	@Before
	public void setup() {
		listAppender = new ListAppender<>();

		logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(listAppender);
		logger.setLevel(Level.DEBUG);

		listAppender.start();

		when(deviceWrapper.getProperties()).thenReturn(snmpProperties);
		when(deviceWrapper.getProperties().getIpAddress()).thenReturn("127.0.0.1");
		when(deviceWrapper.getProperties().getPort()).thenReturn("161");
		when(gatewaySnmpProperties.getCommunityTarget()).thenReturn("public");
	}

	@Test
	public void shouldLogErrorForInvalidSnmpVersion() throws IOException {
		String errorMsg = "Invalid SNMP Version assigned to device 127.0.0.1";

		when(snmpProperties.getVersion()).thenReturn(4);

		// Action
		new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper, variableBindingList);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldCreateUDPTransportMappingWhenListenerProtocolIsUDP() throws IOException {
		when(gatewaySnmpProperties.isTrapListenerProtocolTcp()).thenReturn(false);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		assertEquals(UdpAddress.class, ReflectionTestUtils.getField(snmpDevicePoller, "address").getClass());
		assertEquals(DefaultUdpTransportMapping.class,
				ReflectionTestUtils.getField(snmpDevicePoller, "transport").getClass());
	}

	@Test
	public void shouldCreateTCPTransportMappingWhenListenerProtocolIsTCP() throws IOException {
		when(gatewaySnmpProperties.isTrapListenerProtocolTcp()).thenReturn(true);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		assertEquals(TcpAddress.class, ReflectionTestUtils.getField(snmpDevicePoller, "address").getClass());
		assertEquals(DefaultTcpTransportMapping.class,
				ReflectionTestUtils.getField(snmpDevicePoller, "transport").getClass());
	}

	@Test
	public void shouldConfigureCommunityTargetForV1V2() throws IOException {
		when(snmpProperties.getVersion()).thenReturn(1);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		CommunityTarget target = (CommunityTarget) ReflectionTestUtils.getField(snmpDevicePoller, "target");

		assertEquals(1, target.getVersion());
		assertEquals("127.0.0.1/161", target.getAddress().toString());
		assertEquals("public", target.getCommunity().toString());
	}

	@Test
	public void shouldConfigureTargetForNoAuthNoPriv() throws IOException {
		DeviceAuthentication auth = new DeviceAuthentication();
		ReflectionTestUtils.setField(auth, "engineId", "12345");
		ReflectionTestUtils.setField(auth, "username", "testUser");
		ReflectionTestUtils.setField(auth, "securityLevel", 1);

		when(snmpProperties.getVersion()).thenReturn(3);
		when(snmpProperties.getAuth()).thenReturn(auth);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		Snmp snmp = (Snmp) ReflectionTestUtils.getField(snmpDevicePoller, "snmp");
		Target target = (Target) ReflectionTestUtils.getField(snmpDevicePoller, "target");
		UsmUserEntry userUserEntry = snmp.getUSM().getUser(new OctetString("12345"), new OctetString("testUser"));

		assertNotNull(target);
		assertEquals(3, target.getVersion());
		assertEquals(1, target.getSecurityLevel());
		assertEquals("testUser", target.getSecurityName().toString());
		assertEquals("127.0.0.1/161", target.getAddress().toString());

		assertNotNull(userUserEntry);
		UsmUser user = userUserEntry.getUsmUser();

		assertNull(user.getLocalizationEngineID());
		assertNull(user.getAuthenticationProtocol());
		assertNull(user.getAuthenticationPassphrase());
		assertNull(user.getPrivacyProtocol());
		assertNull(user.getPrivacyPassphrase());
		assertEquals("testUser", user.getSecurityName().toString());
	}

	@Test
	public void shouldConfigureTargetForAuthNoPriv() throws IOException {
		DeviceAuthentication auth = new DeviceAuthentication();
		ReflectionTestUtils.setField(auth, "engineId", "12345");
		ReflectionTestUtils.setField(auth, "username", "snmpUser");
		ReflectionTestUtils.setField(auth, "securityLevel", 2);
		ReflectionTestUtils.setField(auth, "authProtocol", 1);
		ReflectionTestUtils.setField(auth, "authPassword", "authPass");

		when(snmpProperties.getVersion()).thenReturn(3);
		when(snmpProperties.getAuth()).thenReturn(auth);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		Snmp snmp = (Snmp) ReflectionTestUtils.getField(snmpDevicePoller, "snmp");
		Target target = (Target) ReflectionTestUtils.getField(snmpDevicePoller, "target");
		UsmUserEntry userUserEntry = snmp.getUSM().getUser(new OctetString("12345"), new OctetString("snmpUser"));

		assertNotNull(target);
		assertEquals(3, target.getVersion());
		assertEquals(2, target.getSecurityLevel());
		assertEquals("snmpUser", target.getSecurityName().toString());
		assertEquals("127.0.0.1/161", target.getAddress().toString());

		assertNotNull(userUserEntry);
		UsmUser user = userUserEntry.getUsmUser();

		assertEquals("snmpUser", user.getSecurityName().toString());
		assertEquals(AuthMD5.ID, user.getAuthenticationProtocol());
		assertEquals("e5:d2:ae:40:b6:f3:a6:29:5b:7b:71:b3:ce:d7:02:1e", user.getAuthenticationPassphrase().toString());
		assertEquals("12345", user.getLocalizationEngineID().toString());
		assertNull(user.getPrivacyProtocol());
		assertNull(user.getPrivacyPassphrase());
	}

	public void shouldConfigureTargetForAuthPriv() throws IOException {
		DeviceAuthentication auth = new DeviceAuthentication();
		ReflectionTestUtils.setField(auth, "engineId", "12345");
		ReflectionTestUtils.setField(auth, "username", "adminUser");
		ReflectionTestUtils.setField(auth, "securityLevel", 3);
		ReflectionTestUtils.setField(auth, "authProtocol", 2);
		ReflectionTestUtils.setField(auth, "authPassword", "authPass");
		ReflectionTestUtils.setField(auth, "privProtocol", 4);
		ReflectionTestUtils.setField(auth, "privPassword", "privPass");

		when(snmpProperties.getVersion()).thenReturn(3);
		when(snmpProperties.getAuth()).thenReturn(auth);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		Snmp snmp = (Snmp) ReflectionTestUtils.getField(snmpDevicePoller, "snmp");
		Target target = (Target) ReflectionTestUtils.getField(snmpDevicePoller, "target");
		UsmUserEntry userUserEntry = snmp.getUSM().getUser(new OctetString("12345"), new OctetString("adminUser"));

		assertNotNull(target);
		assertEquals(3, target.getVersion());
		assertEquals(2, target.getSecurityLevel());
		assertEquals("adminUser", target.getSecurityName().toString());
		assertEquals("127.0.0.1/161", target.getAddress().toString());

		assertNotNull(userUserEntry);
		UsmUser user = userUserEntry.getUsmUser();

		assertEquals("adminUser", user.getSecurityName().toString());
		assertEquals(AuthSHA.ID, user.getAuthenticationProtocol());
		assertEquals("e5:d2:ae:40:b6:f3:a6:29:5b:7b:71:b3:ce:d7:02:1e", user.getAuthenticationPassphrase().toString());
		assertEquals("12345", user.getLocalizationEngineID().toString());
		assertEquals(PrivAES256.ID, user.getPrivacyProtocol());
		assertEquals("privPass", user.getPrivacyPassphrase().toString());
	}

	@Test
	public void shouldConfigureVariableList() throws IOException {
		List<VariableBinding> variableBindingList = new Vector<>();
		VariableBinding variable1 = new VariableBinding(new OID("1.3.6.1.4.1.52032.1.1.1.0"));
		VariableBinding variable2 = new VariableBinding(new OID("1.3.6.1.4.1.52032.1.1.2.0"));
		variableBindingList.add(variable1);
		variableBindingList.add(variable2);

		when(snmpProperties.getVersion()).thenReturn(1);

		// Action
		SnmpDevicePoller snmpDevicePoller = new SnmpDevicePoller(gatewaySnmpProperties, deviceWrapper,
				variableBindingList);

		PDU pdu = (PDU) ReflectionTestUtils.getField(snmpDevicePoller, "pdu");

		assertEquals(2, pdu.getVariableBindings().size());
	}

	@After
	public void tearDown() {
		listAppender.stop();
		listAppender.list.clear();
		logger.detachAppender(listAppender);
	}

	private boolean checkLogExist(Level logLevel, String errorMsg) {
		AtomicBoolean found = new AtomicBoolean(false);
		listAppender.list.forEach(logEvent -> {
			if (logEvent.getFormattedMessage().equalsIgnoreCase(errorMsg)) {
				found.set(true);
				assertEquals(logLevel, logEvent.getLevel());
			}
		});

		return found.get();
	}
}
