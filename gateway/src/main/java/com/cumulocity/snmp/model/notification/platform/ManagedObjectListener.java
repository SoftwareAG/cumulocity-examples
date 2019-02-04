package com.cumulocity.snmp.model.notification.platform;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class ManagedObjectListener {
    public void onUpdate(Object value) throws InvocationTargetException, IllegalAccessException {

    }

    public void onDelete() {

    }

    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }
}
