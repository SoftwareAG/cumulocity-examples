package com.cumulocity.agent.snmp.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        persistentFilePath.getParent().toFile().delete();
    }

    @Test(expected=NullPointerException.class)
    public void shouldCreateFailForNullMapName() {
        try(AbstractMapImplForTest obj = new AbstractMapImplForTest(null, persistentFilePath.toFile())) {
        }
    }

    @Test(expected=NullPointerException.class)
    public void shouldCreateFailForNullPersistenceFile() {
        try(AbstractMapImplForTest obj = new AbstractMapImplForTest("SOME QUEUE", null)){
        }
    }

    @Test
    public void shouldCreatePersistenceFileIfRequired() {
        Path filePath = Paths.get(
                System.getProperty("user.home"),
                ".snmp",
                "test",
                AbstractMapImplForTest.class.getSimpleName().toLowerCase() + ".dat");
        filePath.toFile().delete();
        filePath.getParent().toFile().delete();
        assertFalse(filePath.getParent().toFile().exists());

        AbstractMapImplForTest map = null;
        try {

            map = new AbstractMapImplForTest("SOME QUEUE", filePath.toFile());

            assertTrue(filePath.getParent().toFile().exists());
        } finally {
            if(map != null) {
                map.close();
            }
            filePath.toFile().delete();
            filePath.getParent().toFile().delete();
        }
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
        abstractMapImplForTest.remove("KEY_ONE");

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

    @Test
    public void shouldApplyOnForEach() {
        Map<String, String> expectedElements = createMapWithEntries(10);
        abstractMapImplForTest.putAll(expectedElements);

        final Map<String, String> acceptedElements = new HashMap<>();
        abstractMapImplForTest.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String key, String value) {
                acceptedElements.put(key, value);
            }
        });

        assertEquals(expectedElements, acceptedElements);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReplaceAll() {
        Map<String, String> expectedElements = createMapWithEntries(10);
        abstractMapImplForTest.putAll(expectedElements);

        abstractMapImplForTest.replaceAll(new BiFunction<String, String, String>() {
            @Override
            public String apply(String key, String value) {
                return "REPLACED";
            }
        });
    }

    @Test
    public void shouldComputeIfAbsent() {
        Function<String, String> computeFunction = new Function<String, String>() {
            @Override
            public String apply(String key) {
                return "NEW COMPUTED VALUE FOR KEY " + key;
            }
        };

        abstractMapImplForTest.put("KEY_PRESENT", "VALUE_ONE");

        // computeIfAbsent when key present
        String newValue = abstractMapImplForTest.computeIfAbsent("KEY_PRESENT", computeFunction);
        assertEquals("VALUE_ONE", newValue);

        // computeIfAbsent when key absent
        newValue = abstractMapImplForTest.computeIfAbsent("KEY_ABSENT", computeFunction);
        assertEquals("NEW COMPUTED VALUE FOR KEY KEY_ABSENT", newValue);
        assertTrue(abstractMapImplForTest.containsKey("KEY_ABSENT"));
    }

    @Test
    public void shouldComputeIfPresent() {
        BiFunction<String, String, String> computeFunction = new BiFunction<String, String, String>() {
            @Override
            public String apply(String key, String oldValue) {
                return "NEW COMPUTED VALUE FOR KEY " + key;
            }
        };

        abstractMapImplForTest.put("KEY_PRESENT", "VALUE_ONE");

        // computeIfPresent when key present
        String newValue = abstractMapImplForTest.computeIfPresent("KEY_PRESENT", computeFunction);
        assertEquals("NEW COMPUTED VALUE FOR KEY KEY_PRESENT", newValue);

        // computeIfPresent when key absent
        String oldValue = abstractMapImplForTest.computeIfPresent("KEY_ABSENT", computeFunction);
        assertNull(oldValue);
        assertEquals(null, abstractMapImplForTest.get("KEY_ABSENT"));
    }

    @Test
    public void shouldCompute() {
        BiFunction<String, String, String> computeFunction = new BiFunction<String, String, String>() {
            @Override
            public String apply(String key, String oldValue) {
                return "NEW COMPUTED VALUE FOR KEY " + key;
            }
        };

        abstractMapImplForTest.put("KEY_PRESENT", "VALUE_ONE");

        // compute when key present
        String newValue = abstractMapImplForTest.compute("KEY_PRESENT", computeFunction);
        assertEquals("NEW COMPUTED VALUE FOR KEY KEY_PRESENT", newValue);

        // compute when key absent
        newValue = abstractMapImplForTest.compute("KEY_ABSENT", computeFunction);
        assertEquals("NEW COMPUTED VALUE FOR KEY KEY_ABSENT", newValue);
        assertTrue(abstractMapImplForTest.containsKey("KEY_ABSENT"));
    }

    @Test
    public void shouldMerge() {
        BiFunction<String, String, String> computeFunction = new BiFunction<String, String, String>() {
            @Override
            public String apply(String oldValue, String newValue) {
                return "NEW COMPUTED VALUE " + newValue;
            }
        };

        abstractMapImplForTest.put("KEY_PRESENT", "VALUE_ONE");

        // merge when key present
        String newValue = abstractMapImplForTest.merge("KEY_PRESENT", "NEW_VALUE_ONE", computeFunction);
        assertEquals("NEW COMPUTED VALUE NEW_VALUE_ONE", newValue);
        assertEquals("NEW COMPUTED VALUE NEW_VALUE_ONE", abstractMapImplForTest.get("KEY_PRESENT"));

        // merge when key absent
        newValue = abstractMapImplForTest.merge("KEY_ABSENT", "NEW_VALUE_ONE", computeFunction);
        assertEquals("NEW_VALUE_ONE", newValue);
        assertEquals("NEW_VALUE_ONE", abstractMapImplForTest.get("KEY_ABSENT"));
    }

    private Map<String, String> createMapWithEntries(int count) {
        Map<String, String> map = new HashMap<>(count);

        for(int i = 0; i< count; i++) {
            map.put("KEY_" + String.valueOf(i), "VALUE_" + String.valueOf(i));
        }

        return map;
    }

    private class AbstractMapImplForTest extends AbstractMap<String, String> {
        AbstractMapImplForTest(String queueName, File persistenceFile) {
            super(queueName,
                    String.class,
                    100,
                    String.class,
                    10_000,
                    10,
                    persistenceFile
            );
        }

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