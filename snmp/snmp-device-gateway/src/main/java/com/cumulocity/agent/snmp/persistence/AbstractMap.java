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

package com.cumulocity.agent.snmp.persistence;

import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Map implementation backed by the persistence Chronicle Map
 *
 * @param <K> Key
 * @param <V> Value
 */
@Slf4j
public abstract class AbstractMap<K, V> implements ConcurrentMap<K, V>, AutoCloseable {

    private final String name;

    private final File persistenceFile;

    private final ChronicleMap<K, V> chronicleMap;


    public AbstractMap(String mapName, Class<K> keyClass, double averageKeySize, Class<V> typeClass, double averageValueSize, long entries, File persistenceFile) {
        if (mapName == null) {
            throw new NullPointerException("mapName");
        }
        this.name = mapName;

        if (persistenceFile == null) {
            throw new NullPointerException("persistenceFile");
        }
        this.persistenceFile = persistenceFile;

        if (!persistenceFile.getParentFile().exists()) {
            persistenceFile.getParentFile().mkdirs();
        }

        log.info("Creating/Loading '{}' Map, backed by the file '{}'", this.name, persistenceFile.getPath());
        try {
            chronicleMap = ChronicleMap
                    .of(keyClass, typeClass)
                    .name(this.name)
                    .averageKeySize(averageKeySize)
                    .averageValueSize(averageValueSize)
                    .entries(entries)
                    .createOrRecoverPersistedTo(persistenceFile);
        } catch (IOException ioe) {
            String message = "Error while creating/loading '" + this.name + "' Map, backed by the file '" + persistenceFile.getPath() + "'";
            log.error(message, ioe);
            throw new IllegalStateException(message, ioe);
        }
    }

    public String getName() {
        return this.name;
    }

    public File getPersistenceFile() {
        return this.persistenceFile;
    }

    @Override
    public int size() {
        return chronicleMap.size();
    }

    @Override
    public boolean isEmpty() {
        return chronicleMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return chronicleMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return chronicleMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return chronicleMap.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        return chronicleMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return chronicleMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        chronicleMap.putAll(m);
    }

    @Override
    public void clear() {
        chronicleMap.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return chronicleMap.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return chronicleMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return chronicleMap.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return chronicleMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        chronicleMap.forEach(action);
    }

    @Override
    public V putIfAbsent(@NotNull K key, V value) {
        return chronicleMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(@NotNull Object key, Object value) {
        return chronicleMap.remove(key, value);
    }

    @Override
    public boolean replace(@NotNull K key, @NotNull V oldValue, @NotNull V newValue) {
        return chronicleMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(@NotNull K key, @NotNull V value) {
        return chronicleMap.replace(key, value);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("replaceAll");
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return chronicleMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return chronicleMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return chronicleMap.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return chronicleMap.merge(key, value, remappingFunction);
    }

    @Override
    public void close() {
        log.info("'{}' Map closed.", this.name);
        chronicleMap.close();
    }
}
