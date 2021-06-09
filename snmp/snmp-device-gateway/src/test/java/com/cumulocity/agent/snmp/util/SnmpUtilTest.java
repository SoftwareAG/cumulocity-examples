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

package com.cumulocity.agent.snmp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class SnmpUtilTest {

	private Logger logger;

	private ListAppender<ILoggingEvent> listAppender;

	@Before
	public void setup() {
		listAppender = new ListAppender<>();

		logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(listAppender);
		logger.setLevel(Level.DEBUG);

		listAppender.start();
	}

	@Test
	public void shoulReturnTrueForValidSnmpVersion() {
		assertTrue(SnmpUtil.isValidSnmpVersion(SnmpConstants.version1));
		assertTrue(SnmpUtil.isValidSnmpVersion(SnmpConstants.version2c));
		assertTrue(SnmpUtil.isValidSnmpVersion(SnmpConstants.version3));
	}

	@Test
	public void shoulReturnFalseForInValidSnmpVersion() {
		assertFalse(SnmpUtil.isValidSnmpVersion(10));
	}

	@Test
	public void shoulReturnTrueForValidAuthProtocol() {
		assertTrue(SnmpUtil.isValidAuthProtocol(1));
		assertTrue(SnmpUtil.isValidAuthProtocol(2));
	}

	@Test
	public void shoulReturnFalseForInValidAuthProtocol() {
		assertFalse(SnmpUtil.isValidAuthProtocol(20));
	}

	@Test
	public void shoulReturnValidAuthProtocolOID() {
		assertEquals(AuthMD5.ID, SnmpUtil.getAuthProtocolOid(1));
		assertEquals(AuthSHA.ID, SnmpUtil.getAuthProtocolOid(2));
	}

	@Test
	public void shoulReturnNullForInValidAuthProtocol() {
		String errorMsg = "Unsupported 30 authentication protocol selected. "
				+ "Supported protocols are usmHMACMD5AuthProtocol as MD5 and usmHMACSHAAuthProtocol as SHA";
		assertNull(SnmpUtil.getAuthProtocolOid(30));
		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shoulReturnTrueForValidPrivacyProtocol() {
		assertTrue(SnmpUtil.isValidPrivacyProtocol(1));
		assertTrue(SnmpUtil.isValidPrivacyProtocol(2));
		assertTrue(SnmpUtil.isValidPrivacyProtocol(3));
		assertTrue(SnmpUtil.isValidPrivacyProtocol(4));
	}

	@Test
	public void shoulReturnFalseForInValidPrivacyProtocol() {
		assertFalse(SnmpUtil.isValidPrivacyProtocol(50));
	}

	@Test
	public void shoulReturnValidPrivacyProtocolOID() {
		assertEquals(PrivDES.ID, SnmpUtil.getPrivacyProtocolOid(1));
		assertEquals(PrivAES128.ID, SnmpUtil.getPrivacyProtocolOid(2));
		assertEquals(PrivAES192.ID, SnmpUtil.getPrivacyProtocolOid(3));
		assertEquals(PrivAES256.ID, SnmpUtil.getPrivacyProtocolOid(4));
	}

	@Test
	public void shoulReturnNullForInValidPrivacyProtocol() {
		String errorMsg = "Unsupported 50 privacy protocol id found. "
				+ "Supported ones are 1 for DES, 2 for AES128, 3 for AES192 and 4 for AES256";
		assertNull(SnmpUtil.getPrivacyProtocolOid(50));
		assertTrue(checkLogExist(Level.ERROR, errorMsg));
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
