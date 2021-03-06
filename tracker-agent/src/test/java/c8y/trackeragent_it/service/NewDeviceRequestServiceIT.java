/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cumulocity.sdk.client.PlatformImpl;

import c8y.trackeragent_it.TestSettings;
import c8y.trackeragent_it.TrackerITSupport;
import c8y.trackeragent_it.config.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class })
@Ignore
public class NewDeviceRequestServiceIT {
    
    @Autowired
    private TestSettings testSettings;

    @Autowired
    private DeviceCredentialsApi deviceCredentialsApi;
    
    private NewDeviceRequestService newDeviceRequestService;
    
    @Before
    public void init() {
        PlatformImpl platform = TrackerITSupport.platform(testSettings);
        newDeviceRequestService = new NewDeviceRequestService(platform, testSettings, deviceCredentialsApi);
        newDeviceRequestService.deleteAll();
    }
    
    @Test
    public void shouldGetAllNewDeviceRequest() throws Exception {
        newDeviceRequestService.create("abc");
        newDeviceRequestService.create("cde");
        
        assertThat(newDeviceRequestService.getAll()).hasSize(2);
        
        newDeviceRequestService.delete("abc");
        newDeviceRequestService.delete("cde");
    }
    
    @Test
    public void shouldDeleteAllNewDeviceRequest() throws Exception {
        newDeviceRequestService.create("abc");
        newDeviceRequestService.create("cde");
        
        newDeviceRequestService.deleteAll();
        
        assertThat(newDeviceRequestService.getAll()).hasSize(0);
    }

}
