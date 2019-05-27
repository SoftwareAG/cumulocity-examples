package com.cumulocity.snmp.integration.platform.service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.integration.notification.RealtimeBroadcaster;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.removeIf;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.MapUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryMockService implements Closeable {

    private final RealtimeBroadcaster realtimeBroadcaster;

    private List<ManagedObjectRepresentation> store = new CopyOnWriteArrayList<>();

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public ManagedObjectRepresentation store(String type, ManagedObjectRepresentation managedObject) {
        managedObject.setType(type);
        return store(managedObject);
    }

    public ManagedObjectRepresentation store(ManagedObjectRepresentation managedObject) {
        final int id = RandomUtils.nextInt(1, 10000);
        managedObject.setId(GId.asGId(id));
        managedObject.setSelf(managedObject.getSelf() + "/" + id);

        final ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
        childDevices.setSelf(managedObject.getSelf() + "/childDevices");

        managedObject.setChildDevices(childDevices);
        store.add(managedObject);
        return managedObject;
    }

    public ManagedObjectRepresentation update(final GId id, final ManagedObjectRepresentation representation) {
        final Optional<ManagedObjectRepresentation> existingRepresentation = findById(id);
        Preconditions.checkArgument(existingRepresentation.isPresent());
        store.remove(existingRepresentation.get());
        final ManagedObjectRepresentation merged = mergeChanges(representation, existingRepresentation.get());
        store.add(merged);
        return merged;
    }

    public ManagedObjectCollectionRepresentation findByType(final String type) {
        return createManagedObjectCollection(from(store)
                .filter(new Predicate<ManagedObjectRepresentation>() {
                    @Override
                    public boolean apply(final ManagedObjectRepresentation input) {
                        return type.equals(input.getType());
                    }
                })
                .toArray(ManagedObjectRepresentation.class));
    }

    @Override
    public void close() {
        clear();
    }

    @SneakyThrows
    public void clear() {
        final boolean b = executor.awaitTermination(5, TimeUnit.SECONDS);
        store.clear();
    }

    public Optional<ManagedObjectRepresentation> findById(final GId id) {
        return from(store).firstMatch(byDeviceId(id));
    }

    public void addChildDevice(final GId id, final ManagedObjectRepresentation savedChild) {
        final ManagedObjectReferenceRepresentation children = new ManagedObjectReferenceRepresentation();
        children.setManagedObject(savedChild);
        addChildDevice(id, children);
    }

    public void addChildDevice(final GId id, final ManagedObjectReferenceRepresentation children) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final Optional<ManagedObjectRepresentation> root = findById(id);
                Preconditions.checkArgument(root.isPresent(), "Cannot add child device to non existing managed object");

                root.get().getChildDevices().setReferences(asList(children));

                log.info("Sending update notification {}", root.get().getId());
                realtimeBroadcaster.sendUpdate(root.get());
            }
        });
    }

    public void deleteById(final GId id) {
        removeIf(store, byDeviceId(id));
        realtimeBroadcaster.sendDelete(id.getValue());
    }

    private ManagedObjectCollectionRepresentation createManagedObjectCollection(ManagedObjectRepresentation... e) {
        final ManagedObjectCollectionRepresentation result = new ManagedObjectCollectionRepresentation();
        result.setManagedObjects(asList(e));
        return result;
    }

    private Predicate<ManagedObjectRepresentation> byDeviceId(final GId id) {
        return new Predicate<ManagedObjectRepresentation>() {
            @Override
            public boolean apply(@Nullable final ManagedObjectRepresentation input) {
                return id.getValue().equals(input.getId().getValue());
            }
        };
    }

    private ManagedObjectRepresentation mergeChanges(final ManagedObjectRepresentation newMo, final ManagedObjectRepresentation existingMo) {
        final ManagedObjectRepresentation merged = new ManagedObjectRepresentation();
        merged.setId(existingMo.getId());
        merged.setType(existingMo.getType());
        if (isNotEmpty(newMo.getAttrs())) {
            merged.setAttrs(newMo.getAttrs());
        }
        return merged;
    }
}
