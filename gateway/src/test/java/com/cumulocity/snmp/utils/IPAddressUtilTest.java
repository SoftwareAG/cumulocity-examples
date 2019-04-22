package com.cumulocity.snmp.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IPAddressUtilTest {

    @Test
    public void shouldCheckIpAddressValidity(){
        Assert.assertTrue(IPAddressUtil.isValid("192.168.2.2"));
        Assert.assertFalse(IPAddressUtil.isValid("192.168.2"));
    }

    @Test
    public void getNextIpAddress(){
        IPAddressUtil startIP = new IPAddressUtil("192.168.1.1");
        IPAddressUtil startIP1 = new IPAddressUtil("192.168.1.1");
        Assert.assertTrue(startIP.equals(startIP1));
        Assert.assertEquals(startIP.next().toString(),"192.168.1.2");
    }
}