package com.cumulocity.snmp.utils;

import com.googlecode.ipv6.IPv6Address;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IPAddressUtilTest {

    @Test
    public void shouldCheckForValidIpv4() {
        Assert.assertTrue(IPAddressUtil.isValidIPv4("192.168.2.2"));
        Assert.assertFalse(IPAddressUtil.isValidIPv4("192.168.2"));
    }

    @Test
    public void shouldCheckForValidIpv6() {
        Assert.assertTrue(IPAddressUtil.isValidIPv6("fe80::c97d:9b03:fae8:cb8e"));
        Assert.assertFalse(IPAddressUtil.isValidIPv6("192.168.2.1"));
    }

    @Test
    public void getNextIpv4() {
        IPAddressUtil startIP = new IPAddressUtil("192.168.1.1");
        IPAddressUtil startIP1 = new IPAddressUtil("192.168.1.1");
        Assert.assertTrue(startIP.equals(startIP1));
        Assert.assertEquals(startIP.next().toString(), "192.168.1.2");
    }

    @Test
    public void getNextIpv6() {
        IPv6Address startIP = IPv6Address.fromString("fe80::c97d:9b03:fae8:cb8e");
        IPv6Address startIP1 = IPv6Address.fromString("fe80::c97d:9b03:fae8:cb8e");
        Assert.assertTrue(startIP.equals(startIP1));
        Assert.assertEquals(startIP.add(1).toString(), "fe80::c97d:9b03:fae8:cb8f");
    }
}