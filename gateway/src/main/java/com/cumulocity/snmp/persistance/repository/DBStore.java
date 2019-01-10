package com.cumulocity.snmp.persistance.repository;

import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.persistance.model.PersistableTypeMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static com.cumulocity.snmp.repository.configuration.RepositoryConfiguration.findConfSubdirectory;
import static java.util.concurrent.TimeUnit.DAYS;

@Slf4j
@Component
public class DBStore<T> {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersistableTypeMetadataProvider metadataProvider;

    @Autowired
    private GatewayConfigurationProperties config;

    private volatile DB db;

    private LoadingCache<Pair<PersistableTypeMetadata, String>, Map<String, T>> store = CacheBuilder.newBuilder()
            .expireAfterAccess(1, DAYS)
            .build(new CacheLoader<Pair<PersistableTypeMetadata, String>, Map<String, T>>() {
                public Map<String, T> load(final Pair<PersistableTypeMetadata, String> type) throws Exception {
                    if (type.getLeft().isInMemory()) {
                        return new ConcurrentHashMap<>();
                    } else {
                        return db().hashMapCreate(type.getRight())
                                .keySerializer(Serializer.STRING)
                                .valueSerializer(new PersistableJacksonSerializer<>(objectMapper, metadataProvider))
                                .makeOrGet();
                    }
                }
            });

    public Map<String, T> get(Class clazz) throws ExecutionException {
        final PersistableTypeMetadata typeMetadata = metadataProvider.getTypeMetadata(clazz);
        final String name = typeMetadata.getStoreName();
        return store.get(Pair.of(typeMetadata, name));
    }

    public void commit() {
        try {
            db().commit();
        } catch (final DBException ex) {
            log.error(ex.getMessage());
            close();
        }
    }

    public void rollback() {
        try {
            db().rollback();
        } catch (final DBException ex) {
            log.error(ex.getMessage());
            close();
        }
    }

    @Synchronized
    public void close() {
        try {
            db().close();
        } catch (final DBException ex) {
            log.error(ex.getMessage());
            db = null;
        }
    }

    public void clearAll() {
        clearAll(db());
    }

    private void clearAll(DB db) {
        for (final Map<String, T> map : this.store.asMap().values()) {
            map.clear();
        }
        db.commit();
    }

    @Synchronized
    protected DB db() {
        if (db == null) {
            try {
                final File dbDirectory = findConfSubdirectory("db");
                final File file = new File(dbDirectory, config.getIdentifier() + ".db");
                final DB result = DBMaker.fileDB(file)
                        .closeOnJvmShutdown()
                        .fileLockDisable()
                        .make();
                db = result;
            } catch (final Exception ex) {
                log.error(ex.getLocalizedMessage(), ex);
                throw Throwables.propagate(ex);
            }
        }
        return db;
    }
}
