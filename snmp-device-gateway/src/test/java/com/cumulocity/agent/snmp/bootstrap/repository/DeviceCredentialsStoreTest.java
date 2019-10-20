package com.cumulocity.agent.snmp.bootstrap.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.config.GatewayProperties;

@RunWith(MockitoJUnitRunner.class)
public class DeviceCredentialsStoreTest {

    @Mock
    private GatewayProperties gatewayProperties;

    private DeviceCredentialsStore deviceCredentialsStore;

    private Path persistentFilePath;


    @Before
    public void setUp() {
        persistentFilePath = Paths.get(
                System.getProperty("user.home"),
                ".snmp",
                this.getClass().getSimpleName().toLowerCase(),
                "chronicle",
                "maps",
                "device-credentials-store.dat");

        Mockito.when(gatewayProperties.getGatewayIdentifier()).thenReturn(this.getClass().getSimpleName());
        deviceCredentialsStore = Mockito.spy(new DeviceCredentialsStore(gatewayProperties));
    }

    @After
    public void tearDown() {
        deviceCredentialsStore.close();

        persistentFilePath.toFile().delete();
    }

    @Test
    public void shouldCreateDeviceCredentialsStoreWithCorrectName() {
        assertEquals("device-credentials-store", deviceCredentialsStore.getName());
    }

    @Test
    public void shouldCreateAndPersistInFile() {
        assertTrue(deviceCredentialsStore.getPersistenceFile().exists());
        assertEquals(persistentFilePath.toString(), deviceCredentialsStore.getPersistenceFile().getPath());
    }

    @Test
    public void shouldCreateEmptyStore() {
        assertNull(deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));
        assertEquals(0, deviceCredentialsStore.size());
        assertTrue(deviceCredentialsStore.isEmpty());
        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("ONE")));
    }

    @Test
    public void shouldPutSuccessfully() {
        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("ONE"));

        assertEquals(1, deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertEquals(createValue("ONE"), deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));
    }

    @Test
    public void shouldPutMultipleSuccessfully() {
        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("ONE"));

        assertEquals(1, deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertEquals(createValue("ONE"), deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));

        //PUT AGAIN
        deviceCredentialsStore.put(createDeviceCredentialsKey("TWO"), createValue("TWO"));

        assertEquals(2, deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertEquals(createValue("ONE"), deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("TWO")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("TWO")));
        assertEquals(createValue("TWO"), deviceCredentialsStore.get(createDeviceCredentialsKey("TWO")));
    }

    @Test
    public void shouldRemoveSuccessfully() {
        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("ONE"));

        assertEquals(1, deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertEquals(createValue("ONE"), deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));

        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("TWO"));

        assertEquals(1, deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("TWO")));
        assertEquals(createValue("TWO"), deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));

        //REMOVE
        deviceCredentialsStore.remove(createDeviceCredentialsKey("ONE"));

        assertEquals(0, deviceCredentialsStore.size());
        assertTrue(deviceCredentialsStore.isEmpty());
        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertNull(deviceCredentialsStore.get(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("TWO")));
    }

    @Test
    public void shouldPutAllSuccessfully() {
        Map<DeviceCredentialsKey, String> map = createMapWithEntries(10);

        //PUT ALL
        deviceCredentialsStore.putAll(map);

        assertEquals(map.size(), deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());

        for(int i = 0; i< map.size(); i++) {
            assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey(String.valueOf(i))));
            assertTrue(deviceCredentialsStore.containsValue(createValue(String.valueOf(i))));
            assertEquals(createValue(String.valueOf(i)), deviceCredentialsStore.get(createDeviceCredentialsKey(String.valueOf(i))));
        }
    }

    @Test
    public void shouldClearSuccessfully() {
        Map<DeviceCredentialsKey, String> map = createMapWithEntries(10);

        //PUT ALL
        deviceCredentialsStore.putAll(map);

        assertEquals(map.size(), deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());

        //CLEAR
        deviceCredentialsStore.clear();

        assertEquals(0, deviceCredentialsStore.size());
        assertTrue(deviceCredentialsStore.isEmpty());
    }

    @Test
    public void shouldGetAllKeysUsingKeySet() {
        Map<DeviceCredentialsKey, String> map = createMapWithEntries(10);

        //PUT ALL
        deviceCredentialsStore.putAll(map);

        assertEquals(map.size(), deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());

        //KeySet
        assertEquals(map.size(), deviceCredentialsStore.keySet().size());
        for(DeviceCredentialsKey oneKey : deviceCredentialsStore.keySet()) {
            assertTrue(map.containsKey(oneKey));
        }
    }

    @Test
    public void shouldGetAllValuesUsingValues() {
        Map<DeviceCredentialsKey, String> map = createMapWithEntries(10);

        //PUT ALL
        deviceCredentialsStore.putAll(map);

        assertEquals(map.size(), deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());

        //KeySet
        assertEquals(map.size(), deviceCredentialsStore.values().size());
        for(String oneValue : deviceCredentialsStore.values()) {
            assertTrue(map.containsValue(oneValue));
        }
    }

    @Test
    public void shouldGetAllEntriesUsingEntrySet() {
        Map<DeviceCredentialsKey, String> map = createMapWithEntries(10);

        //PUT ALL
        deviceCredentialsStore.putAll(map);

        assertEquals(map.size(), deviceCredentialsStore.size());
        assertFalse(deviceCredentialsStore.isEmpty());

        //KeySet
        assertEquals(map.size(), deviceCredentialsStore.entrySet().size());
        for(Map.Entry<DeviceCredentialsKey, String> oneEntry : deviceCredentialsStore.entrySet()) {
            assertTrue(map.containsKey(oneEntry.getKey()));
            assertTrue(map.containsValue(oneEntry.getValue()));
        }
    }

    @Test
    public void shouldGetOrDefault() {
        //GET OR DEFAULT when not found
        assertEquals(createValue("DEFAULT_ONE"), deviceCredentialsStore.getOrDefault(createDeviceCredentialsKey("ONE"), createValue("DEFAULT_ONE")));

        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("ONE"));

        //GET OR DEFAULT when found
        assertEquals(createValue("ONE"), deviceCredentialsStore.getOrDefault(createDeviceCredentialsKey("ONE"), createValue("DEFAULT_ONE")));
    }

    @Test
    public void shouldPutIfAbsent() {
        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));

        //PUT IF ABSENT
        deviceCredentialsStore.putIfAbsent(createDeviceCredentialsKey("ONE"), createValue("ONE"));

        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("ONE")));

        //PUT IF ABSENT key already present
        deviceCredentialsStore.putIfAbsent(createDeviceCredentialsKey("ONE"), createValue("NEW_ONE"));

        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("NEW_ONE")));
    }


    @Test
    public void shouldReplace() {
        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));

        //Replace
        deviceCredentialsStore.replace(createDeviceCredentialsKey("ONE"), createValue("ONE"));

        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("ONE")));

        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("ONE"));
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));

        //Replace key already present
        deviceCredentialsStore.replace(createDeviceCredentialsKey("ONE"), createValue("NEW_ONE"));

        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("NEW_ONE")));
    }

    @Test
    public void shouldReplaceMatchOnValue() {
        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));

        //Replace
        deviceCredentialsStore.replace(createDeviceCredentialsKey("ONE"), createValue("ONE"), createValue("NEW_ONE"));

        assertFalse(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("NEW_ONE")));

        //PUT
        deviceCredentialsStore.put(createDeviceCredentialsKey("ONE"), createValue("ONE"));
        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));

        //Replace key already present and value matching
        deviceCredentialsStore.replace(createDeviceCredentialsKey("ONE"), createValue("ONE"), createValue("NEW_ONE"));

        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("NEW_ONE")));

        //Replace key already present and value NOT matching
        deviceCredentialsStore.replace(createDeviceCredentialsKey("ONE"), createValue("NOT MATCHING"), createValue("NEW_NEW_ONE"));

        assertTrue(deviceCredentialsStore.containsKey(createDeviceCredentialsKey("ONE")));
        assertTrue(deviceCredentialsStore.containsValue(createValue("NEW_ONE")));
        assertFalse(deviceCredentialsStore.containsValue(createValue("NEW_NEW_ONE")));
    }

    private Map<DeviceCredentialsKey, String> createMapWithEntries(int count) {
        Map<DeviceCredentialsKey, String> map = new HashMap<>(count);

        for(int i = 0; i< count; i++) {
            map.put(createDeviceCredentialsKey(String.valueOf(i)), createValue(String.valueOf(i)));
        }

        return map;
    }

    private DeviceCredentialsKey createDeviceCredentialsKey(String suffix) {
        return new DeviceCredentialsKey("http://developers.cumulocity.com." + suffix, "TENANT_" + suffix, "USER_" + suffix);
    }

    private String createValue(String suffix) {
        return "VALUE_" + suffix;
    }
}