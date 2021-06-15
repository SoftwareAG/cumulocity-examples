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

import com.google.common.net.InetAddresses;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

public class IpAddressUtilTest {

    @Test
    public void forStringShouldReturnNullForNullOrEmptyString() {
        assertNull(IpAddressUtil.forString(null, false));
        assertNull(IpAddressUtil.forString(null));
        assertEquals(InetAddress.getLoopbackAddress(), IpAddressUtil.forString(null, true));

        assertNull(IpAddressUtil.forString("", false));
        assertNull(IpAddressUtil.forString(""));
        assertEquals(InetAddress.getLoopbackAddress(), IpAddressUtil.forString("", true));
    }

    @Test
    public void forStringShouldReturnInetAddressForIPV4String() {
        assertEquals(InetAddresses.forString("192.168.1.16"), IpAddressUtil.forString("192.168.1.16", false));
        assertEquals(InetAddresses.forString("192.168.1.16"), IpAddressUtil.forString("192.168.1.16"));
        assertEquals(InetAddresses.forString("192.168.1.16"), IpAddressUtil.forString("192.168.1.16", true));

        assertEquals(InetAddress.getLoopbackAddress(), IpAddressUtil.forString("127.0.0.1"));
        assertEquals(InetAddress.getLoopbackAddress(), IpAddressUtil.forString("127.0.0.1/8"));

        assertEquals(InetAddresses.forString("192.168.1.16"), IpAddressUtil.forString("192.168.1.16/28", false));
        assertEquals(InetAddresses.forString("192.168.1.16"), IpAddressUtil.forString("192.168.1.16/28"));
        assertEquals(InetAddresses.forString("192.168.1.16"), IpAddressUtil.forString("192.168.1.16/28", true));
    }

    @Test
    public void forStringShouldReturnInetAddressForIPV6String() {
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04", false));
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04"));
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04".toUpperCase())); // UPPER CASE
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04", true));

        assertEquals(InetAddresses.forString("0:0:0:0:0:0:0:1"), IpAddressUtil.forString("::1"));
        assertEquals(InetAddresses.forString("0:0:0:0:0:0:0:1"), IpAddressUtil.forString("::1/128"));

        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04%18", false));
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04%18"));
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04%18".toUpperCase())); // UPPER CASE
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), IpAddressUtil.forString("ee90::caca:afff:aaaa:9a04%18", true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forStringShouldThrowIllegalArgumentExceptionForInvalidIPAddress() {
        IpAddressUtil.forString("aaa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void forStringShouldThrowIllegalArgumentExceptionForLocalhost() {
        IpAddressUtil.forString("localhost");
    }

    @Test
    public void sanitizeShouldReturnNullForNullOrEmptyString() {
        assertNull(IpAddressUtil.sanitizeIpAddress(null, false));
        assertNull(IpAddressUtil.sanitizeIpAddress(null));
        assertEquals(InetAddress.getLoopbackAddress().getHostAddress(), IpAddressUtil.sanitizeIpAddress(null, true));

        assertNull(IpAddressUtil.sanitizeIpAddress("", false));
        assertNull(IpAddressUtil.sanitizeIpAddress(""));
        assertEquals(InetAddress.getLoopbackAddress().getHostAddress(), IpAddressUtil.sanitizeIpAddress("", true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizaShouldThrowIllegalArgumentExceptionForInvalidIPAddress() {
        IpAddressUtil.sanitizeIpAddress("aaa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizaShouldThrowIllegalArgumentExceptionForLocalhost() {
        IpAddressUtil.sanitizeIpAddress("localhost");
    }

    @Test
    public void shouldSanitizeIPAddress() {
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04").getHostAddress(), IpAddressUtil.sanitizeIpAddress("ee90::caca:afff:aaaa:9a04".toUpperCase(), false)); // UPPER CASE
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04").getHostAddress(), IpAddressUtil.sanitizeIpAddress("ee90::caca:afff:aaaa:9a04".toUpperCase())); // UPPER CASE
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04").getHostAddress(), IpAddressUtil.sanitizeIpAddress("ee90::caca:afff:aaaa:9a04".toUpperCase(), true)); // UPPER CASE

        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04").getHostAddress(), IpAddressUtil.sanitizeIpAddress("ee90::caca:afff:aaaa:9a04%18".toUpperCase(), false)); // UPPER CASE
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04").getHostAddress(), IpAddressUtil.sanitizeIpAddress("ee90::caca:afff:aaaa:9a04%18".toUpperCase())); // UPPER CASE
        assertEquals(InetAddresses.forString("ee90::caca:afff:aaaa:9a04").getHostAddress(), IpAddressUtil.sanitizeIpAddress("ee90::caca:afff:aaaa:9a04%18".toUpperCase(), true)); // UPPER CASE
    }

    @Test
    public void shouldCompareIPAddresses() {
        assertTrue(0 == IpAddressUtil.compare(InetAddresses.forString("192.168.1.1"), InetAddresses.forString("192.168.1.1")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("192.168.1.0"), InetAddresses.forString("192.168.1.1")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("192.168.0.1"), InetAddresses.forString("192.168.1.1")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("192.167.1.1"), InetAddresses.forString("192.168.1.1")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("190.168.1.1"), InetAddresses.forString("192.168.1.1")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("255.255.255.0"), InetAddresses.forString("255.255.255.255")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("255.255.0.255"), InetAddresses.forString("255.255.255.255")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("255.0.255.255"), InetAddresses.forString("255.255.255.255")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0.255.255.255"), InetAddresses.forString("255.255.255.255")));

        assertTrue(0 == IpAddressUtil.compare(InetAddresses.forString("0:0:0:0:0:0:0:1"), InetAddresses.forString("0:0:0:0:0:0:0:1")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:0:0:0:0:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:0:0:0:0:0:1"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:0:0:0:0:1:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:0:0:0:1:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:0:0:1:0:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:0:1:0:0:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:0:1:0:0:0:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("0:1:0:0:0:0:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));
        assertTrue(0 == IpAddressUtil.compare(InetAddresses.forString("1:0:0:0:0:0:0:0"), InetAddresses.forString("1:0:0:0:0:0:0:0")));

        assertTrue(0 == IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:fffe:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:fffe:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:ffff:ffff:fffe:ffff:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:ffff:fffe:ffff:ffff:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:ffff:fffe:ffff:ffff:ffff:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff:fffe:ffff:ffff:ffff:ffff:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("fffe:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ee90::caca:afff:aaaa:9a04"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff::caca:afff:aaaa:9a04"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff::caca:afff:aaaa:9a04"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff:ffff::ffff")));
        assertTrue(-1 >= IpAddressUtil.compare(InetAddresses.forString("ffff::caca:afff:aaaa:9a04"), InetAddresses.forString("ffff:ffff:ffff:ffff:ffff::ffff:ffff")));
    }
}