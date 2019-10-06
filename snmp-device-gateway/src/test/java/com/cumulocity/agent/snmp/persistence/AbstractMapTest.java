package com.cumulocity.agent.snmp.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AbstractMapTest {

    private Path persistentFilePath;

    private AbstractMapImplForTest abstractMapImplForTest;


    @Before
    public void setUp() {
        persistentFilePath = Paths.get(
                System.getProperty("user.home"),
                ".snmp",
                "chronicle",
                "maps",
                AbstractMapImplForTest.class.getSimpleName().toLowerCase() + ".dat");

        abstractMapImplForTest = new AbstractMapImplForTest();
    }

    @After
    public void tearDown() {
        abstractMapImplForTest.close();

        persistentFilePath.toFile().delete();
    }

    @Test
    public void shouldCreateMapWithCorrectName() {
        assertEquals(AbstractMapImplForTest.class.getSimpleName(), abstractMapImplForTest.getName());
    }

    @Test
    public void shouldCreateAndPersistInFile() {
        assertTrue(abstractMapImplForTest.getPersistenceFile().exists());
        assertEquals(persistentFilePath.toString(), abstractMapImplForTest.getPersistenceFile().getPath());
    }

    @Test
    public void shouldCreateEmptyMap() {
        assertNull(abstractMapImplForTest.get("KEY_ONE"));
        assertEquals(0, abstractMapImplForTest.size());
        assertTrue(abstractMapImplForTest.isEmpty());
        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_ONE"));
    }

    @Test
    public void shouldPutSuccessfully() {
        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_ONE");

        assertEquals(1, abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertEquals("VALUE_ONE", abstractMapImplForTest.get("KEY_ONE"));
    }

    @Test
    public void shouldPutMultipleSuccessfully() {
        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_ONE");

        assertEquals(1, abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertEquals("VALUE_ONE", abstractMapImplForTest.get("KEY_ONE"));

        //PUT AGAIN
        abstractMapImplForTest.put("KEY_TWO", "VALUE_TWO");

        assertEquals(2, abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertEquals("VALUE_ONE", abstractMapImplForTest.get("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsKey("KEY_TWO"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_TWO"));
        assertEquals("VALUE_TWO", abstractMapImplForTest.get("KEY_TWO"));
    }

    @Test
    public void shouldRemoveSuccessfully() {
        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_ONE");

        assertEquals(1, abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertEquals("VALUE_ONE", abstractMapImplForTest.get("KEY_ONE"));

        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_TWO");

        assertEquals(1, abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_TWO"));
        assertEquals("VALUE_TWO", abstractMapImplForTest.get("KEY_ONE"));

        //REMOVE
        String removed = abstractMapImplForTest.remove("KEY_ONE");

        assertEquals(0, abstractMapImplForTest.size());
        assertTrue(abstractMapImplForTest.isEmpty());
        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertNull(abstractMapImplForTest.get("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_TWO"));
    }

    @Test
    public void shouldPutAllSuccessfully() {
        Map<String, String> map = createMapWithEntries(10);

        //PUT ALL
        abstractMapImplForTest.putAll(map);

        assertEquals(map.size(), abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());

        for(int i = 0; i< map.size(); i++) {
            assertTrue(abstractMapImplForTest.containsKey("KEY_" + String.valueOf(i)));
            assertTrue(abstractMapImplForTest.containsValue("VALUE_" + String.valueOf(i)));
            assertEquals("VALUE_" + String.valueOf(i), abstractMapImplForTest.get("KEY_" + String.valueOf(i)));
        }
    }

    @Test
    public void shouldClearSuccessfully() {
        Map<String, String> map = createMapWithEntries(10);

        //PUT ALL
        abstractMapImplForTest.putAll(map);

        assertEquals(map.size(), abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());

        //CLEAR
        abstractMapImplForTest.clear();

        assertEquals(0, abstractMapImplForTest.size());
        assertTrue(abstractMapImplForTest.isEmpty());
    }

    @Test
    public void shouldGetAllKeysUsingKeySet() {
        Map<String, String> map = createMapWithEntries(10);

        //PUT ALL
        abstractMapImplForTest.putAll(map);

        assertEquals(map.size(), abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());

        //KeySet
        assertEquals(map.size(), abstractMapImplForTest.keySet().size());
        for(String oneKey : abstractMapImplForTest.keySet()) {
            assertTrue(map.containsKey(oneKey));
        }
    }

    @Test
    public void shouldGetAllValuesUsingValues() {
        Map<String, String> map = createMapWithEntries(10);

        //PUT ALL
        abstractMapImplForTest.putAll(map);

        assertEquals(map.size(), abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());

        //KeySet
        assertEquals(map.size(), abstractMapImplForTest.values().size());
        for(String oneValue : abstractMapImplForTest.values()) {
            assertTrue(map.containsValue(oneValue));
        }
    }

    @Test
    public void shouldGetAllEntriesUsingEntrySet() {
        Map<String, String> map = createMapWithEntries(10);

        //PUT ALL
        abstractMapImplForTest.putAll(map);

        assertEquals(map.size(), abstractMapImplForTest.size());
        assertFalse(abstractMapImplForTest.isEmpty());

        //KeySet
        assertEquals(map.size(), abstractMapImplForTest.entrySet().size());
        for(Map.Entry<String, String> oneEntry : abstractMapImplForTest.entrySet()) {
            assertTrue(map.containsKey(oneEntry.getKey()));
            assertTrue(map.containsValue(oneEntry.getValue()));
        }
    }

    @Test
    public void shouldGetOrDefault() {
        //GET OR DEFAULT when not found
        assertEquals("VALUE_DEFAULT_ONE", abstractMapImplForTest.getOrDefault("KEY_ONE", "VALUE_DEFAULT_ONE"));

        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_ONE");

        //GET OR DEFAULT when found
        assertEquals("VALUE_ONE", abstractMapImplForTest.getOrDefault("KEY_ONE", "VALUE_DEFAULT_ONE"));
    }

    @Test
    public void shouldPutIfAbsent() {
        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));

        //PUT IF ABSENT
        abstractMapImplForTest.putIfAbsent("KEY_ONE", "VALUE_ONE");

        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_ONE"));

        //PUT IF ABSENT key already present
        abstractMapImplForTest.putIfAbsent("KEY_ONE", "VALUE_NEW_ONE");

        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_NEW_ONE"));
    }


    @Test
    public void shouldReplace() {
        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));

        //Replace
        abstractMapImplForTest.replace("KEY_ONE", "VALUE_ONE");

        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_ONE"));

        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_ONE");
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));

        //Replace key already present
        abstractMapImplForTest.replace("KEY_ONE", "VALUE_NEW_ONE");

        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_NEW_ONE"));
    }

    @Test
    public void shouldReplaceMatchOnValue() {
        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));

        //Replace
        abstractMapImplForTest.replace("KEY_ONE", "VALUE_ONE", "VALUE_NEW_ONE");

        assertFalse(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_NEW_ONE"));

        //PUT
        abstractMapImplForTest.put("KEY_ONE", "VALUE_ONE");
        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));

        //Replace key already present and value matching
        abstractMapImplForTest.replace("KEY_ONE", "VALUE_ONE", "VALUE_NEW_ONE");

        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_NEW_ONE"));

        //Replace key already present and value NOT matching
        abstractMapImplForTest.replace("KEY_ONE", "VALUE_NOT_MATCHING", "VALUE_NEW_NEW_ONE");

        assertTrue(abstractMapImplForTest.containsKey("KEY_ONE"));
        assertTrue(abstractMapImplForTest.containsValue("VALUE_NEW_ONE"));
        assertFalse(abstractMapImplForTest.containsValue("VALUE_NEW_NEW_ONE"));
    }

    private Map<String, String> createMapWithEntries(int count) {
        Map<String, String> map = new HashMap<>(count);

        for(int i = 0; i< count; i++) {
            map.put("KEY_" + String.valueOf(i), "VALUE_" + String.valueOf(i));
        }

        return map;
    }

    private class AbstractMapImplForTest extends AbstractMap<String, String> {
        @Autowired
        AbstractMapImplForTest() {
            super(AbstractMapImplForTest.class.getSimpleName(),
                    String.class,
                    100,
                    String.class,
                    10_000,
                    10,
                    persistentFilePath.toFile()
            );
        }
    }
}