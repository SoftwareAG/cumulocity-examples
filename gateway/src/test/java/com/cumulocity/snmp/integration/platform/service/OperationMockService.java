package com.cumulocity.snmp.integration.platform.service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.snmp.integration.notification.RealtimeBroadcaster;
import com.cumulocity.snmp.integration.platform.subscription.OperationNotification;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.cumulocity.model.idtype.GId.asGId;
import static com.google.common.collect.FluentIterable.from;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationMockService implements Closeable {

    private final RealtimeBroadcaster realtime;

    private List<OperationRepresentation> store = new CopyOnWriteArrayList<>();

    public OperationRepresentation store(OperationRepresentation data) {
        final int id = RandomUtils.nextInt(1, 10000);
        data.setId(asGId(id));
        data.setSelf(data.getSelf() + "/" + id);
        store.add(data);

        final OperationNotification notification = new OperationNotification(data, "CREATE");
        realtime.sendOperation(data.getDeviceId().getValue(), notification);
        return data;
    }

    public Optional<OperationRepresentation> update(final String id, final OperationRepresentation operationRepresentation) {
        final Optional<OperationRepresentation> existingOperation = findById(asGId(id));
        if (existingOperation.isPresent()) {
            final OperationRepresentation updatedOperation = mergeChanges(operationRepresentation, existingOperation.get());
            store.add(updatedOperation);
            store.remove(existingOperation.get());
            return Optional.of(updatedOperation);
        } else {
            return Optional.absent();
        }
    }

    private OperationRepresentation mergeChanges(final OperationRepresentation newOp, final OperationRepresentation existingOp) {
        final OperationRepresentation merged = new OperationRepresentation();
        merged.setId(existingOp.getId());
        merged.setDeviceId(existingOp.getDeviceId());
        merged.setStatus(existingOp.getStatus());
        merged.getAttrs().putAll(existingOp.getAttrs());
        merged.getAttrs().putAll(newOp.getAttrs());
        if (newOp.getStatus() != null) {
            merged.setStatus(newOp.getStatus());
        }
        return merged;
    }

    public List<OperationRepresentation> getAll() {
        return store;
    }

    public Optional<OperationRepresentation> findById(final GId id) {
        return from(store)
                .firstMatch(new Predicate<OperationRepresentation>() {
                    @Override
                    public boolean apply(final OperationRepresentation input) {
                        return id.equals(input.getId());
                    }
                });
    }

    @Override
    public void close() {
        clear();
    }

    public void clear() {
        store.clear();
    }

}
