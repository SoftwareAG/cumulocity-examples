package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.sdk.client.Platform;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlatformApiProviderTest {

    @Mock
    private PlatformProvider platformProvider;

    @Mock
    private Platform platform;

    @InjectMocks
    private PlatformApiProvider platformApiProvider;

    @Before
    public void serUp() {
        when(platformProvider.getPlatform()).thenReturn(platform);
    }

    @Test
    public void identityApi() {
        platformApiProvider.identityApi();
        verify(platform, times(1)).getIdentityApi();
    }

    @Test
    public void inventoryApi() {
        platformApiProvider.inventoryApi();
        verify(platform, times(1)).getInventoryApi();
    }

    @Test
    public void deviceControlApi() {
        platformApiProvider.deviceControlApi();
        verify(platform, times(1)).getDeviceControlApi();
    }

    @Test
    public void restOperations() {
        platformApiProvider.restOperations();
        verify(platform, times(1)).rest();
    }

    @Test
    public void alarmApi() {
        platformApiProvider.alarmApi();
        verify(platform, times(1)).getAlarmApi();
    }

    @Test
    public void eventApi() {
        platformApiProvider.eventApi();
        verify(platform, times(1)).getEventApi();
    }

    @Test
    public void measurementApi() {
        platformApiProvider.measurementApi();
        verify(platform, times(1)).getMeasurementApi();
    }
}