package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.rest.representation.AbstractExtensibleRepresentation;
import com.cumulocity.snmp.factory.platform.ManagedObjectMapper;
import com.cumulocity.snmp.model.operation.Operation;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationFactory {
    private final ManagedObjectMapper managedObjectMapper;

    public Optional<Operation> create(AbstractExtensibleRepresentation managedObject){
        try {
            return managedObjectMapper.convert(Operation.class, managedObject);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }
}
