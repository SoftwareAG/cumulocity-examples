/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
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

package com.cumulocity.agent.snmp.bootstrap.service;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.bootstrap.repository.DeviceCredentialsStore;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class DeviceCredentialsStoreServiceTest {

    @Mock
    private GatewayProperties gatewayProperties;

    @Mock
    private DeviceCredentialsStore deviceCredentialsStore;

    @Mock
    private GatewayProperties.BootstrapProperties bootstrapProperties;

    private DeviceCredentialsStoreService deviceCredentialsStoreService;


    @Before
    public void setUp() throws Exception {
        deviceCredentialsStoreService = new DeviceCredentialsStoreService(gatewayProperties, deviceCredentialsStore);
    }

    @After
    public void tearDown() throws Exception {
        deviceCredentialsStore.close();
    }

    @Test
    public void shouldStoreCredentialsSuccessfully() {
        String suffix = "ONE";
        Mockito.when(gatewayProperties.getBaseUrl()).thenReturn("http://developers.cumulocity.com." + suffix);
        Mockito.when(gatewayProperties.getBootstrapProperties()).thenReturn(bootstrapProperties);
        Mockito.when(bootstrapProperties.getTenantId()).thenReturn("TENANT_" + suffix);
        Mockito.when(bootstrapProperties.getUsername()).thenReturn("USER_" + suffix);

        DeviceCredentialsRepresentation deviceCredentials = createDeviceCredentialsRepresentation(suffix);
        deviceCredentialsStoreService.store(deviceCredentials);

        DeviceCredentialsKey expectedKey = createDeviceCredentialsKey(suffix);
        Mockito.verify(deviceCredentialsStore).put(Mockito.eq(expectedKey), Mockito.eq(deviceCredentials.toJSON()));
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailToStoreNullCredentials() {
        deviceCredentialsStoreService.store(null);
        Mockito.verifyZeroInteractions(deviceCredentialsStore);
    }

    @Test
    public void shouldFetchCredentialsSuccessfully() {
        String suffix = "ONE";
        Mockito.when(gatewayProperties.getBaseUrl()).thenReturn("http://developers.cumulocity.com." + suffix);
        Mockito.when(gatewayProperties.getBootstrapProperties()).thenReturn(bootstrapProperties);
        Mockito.when(bootstrapProperties.getTenantId()).thenReturn("TENANT_" + suffix);
        Mockito.when(bootstrapProperties.getUsername()).thenReturn("USER_" + suffix);

        DeviceCredentialsKey expectedKey = createDeviceCredentialsKey(suffix);
        DeviceCredentialsRepresentation expectedCredentials = createDeviceCredentialsRepresentation(suffix);
        Mockito.when(deviceCredentialsStore.get(expectedKey)).thenReturn(expectedCredentials.toJSON());

        DeviceCredentialsRepresentation actualCredentials = deviceCredentialsStoreService.fetch();

        Mockito.verify(deviceCredentialsStore).get(expectedKey);

        assertEquals(expectedCredentials.getId(), actualCredentials.getId());
        assertEquals(expectedCredentials.getTenantId(), actualCredentials.getTenantId());
        assertEquals(expectedCredentials.getUsername(), actualCredentials.getUsername());
        assertEquals(expectedCredentials.getPassword(), actualCredentials.getPassword());
    }

    @Test
    public void shouldReturnNullWhenNonExistenetCredentialsAreFetched() {
        String suffix = "ONE";
        Mockito.when(gatewayProperties.getBaseUrl()).thenReturn("http://developers.cumulocity.com." + suffix);
        Mockito.when(gatewayProperties.getBootstrapProperties()).thenReturn(bootstrapProperties);
        Mockito.when(bootstrapProperties.getTenantId()).thenReturn("TENANT_" + suffix);
        Mockito.when(bootstrapProperties.getUsername()).thenReturn("USER_" + suffix);

        DeviceCredentialsKey expectedKey = createDeviceCredentialsKey(suffix);
        Mockito.when(deviceCredentialsStore.get(expectedKey)).thenReturn(null);

        DeviceCredentialsRepresentation actualCredentials = deviceCredentialsStoreService.fetch();

        Mockito.verify(deviceCredentialsStore).get(expectedKey);

        assertNull(actualCredentials);
    }

    @Test
    public void shouldRemoveCredentialsSuccessfully() {
        String suffix = "ONE";
        Mockito.when(gatewayProperties.getBaseUrl()).thenReturn("http://developers.cumulocity.com." + suffix);
        Mockito.when(gatewayProperties.getBootstrapProperties()).thenReturn(bootstrapProperties);
        Mockito.when(bootstrapProperties.getTenantId()).thenReturn("TENANT_" + suffix);
        Mockito.when(bootstrapProperties.getUsername()).thenReturn("USER_" + suffix);

        DeviceCredentialsKey expectedKey = createDeviceCredentialsKey(suffix);
        DeviceCredentialsRepresentation expectedCredentials = createDeviceCredentialsRepresentation(suffix);
        Mockito.when(deviceCredentialsStore.remove(expectedKey)).thenReturn(expectedCredentials.toJSON());

        DeviceCredentialsRepresentation actualCredentials = deviceCredentialsStoreService.remove();

        Mockito.verify(deviceCredentialsStore).remove(expectedKey);

        assertEquals(expectedCredentials.getId(), actualCredentials.getId());
        assertEquals(expectedCredentials.getTenantId(), actualCredentials.getTenantId());
        assertEquals(expectedCredentials.getUsername(), actualCredentials.getUsername());
        assertEquals(expectedCredentials.getPassword(), actualCredentials.getPassword());
    }

    @Test
    public void shouldReturnNullWhenNonExistenetCredentialsAreRemoved() {
        String suffix = "ONE";
        Mockito.when(gatewayProperties.getBaseUrl()).thenReturn("http://developers.cumulocity.com." + suffix);
        Mockito.when(gatewayProperties.getBootstrapProperties()).thenReturn(bootstrapProperties);
        Mockito.when(bootstrapProperties.getTenantId()).thenReturn("TENANT_" + suffix);
        Mockito.when(bootstrapProperties.getUsername()).thenReturn("USER_" + suffix);

        DeviceCredentialsKey expectedKey = createDeviceCredentialsKey(suffix);
        Mockito.when(deviceCredentialsStore.remove(expectedKey)).thenReturn(null);

        DeviceCredentialsRepresentation actualCredentials = deviceCredentialsStoreService.remove();

        Mockito.verify(deviceCredentialsStore).remove(expectedKey);

        assertNull(actualCredentials);
    }

    @Test
    public void shouldCloseDeviceCredentialsStore() {
        deviceCredentialsStoreService.closeDeviceCredentialsStore();

        Mockito.verify(deviceCredentialsStore).close();
    }


    @Test
    public void remove() {
    }

    private DeviceCredentialsKey createDeviceCredentialsKey(String suffix) {
        return new DeviceCredentialsKey("http://developers.cumulocity.com." + suffix, "TENANT_" + suffix, "USER_" + suffix);
    }

    private DeviceCredentialsRepresentation createDeviceCredentialsRepresentation(String suffix) {
        DeviceCredentialsRepresentation credentials = new DeviceCredentialsRepresentation();

        credentials.setId("ID_" + suffix);
        credentials.setTenantId("TENANT_" + suffix);
        credentials.setUsername("USER_" + suffix);
        credentials.setPassword("PASSWORD_" + suffix);

        return credentials;
    }
}