package com.cumulocity.snmp.utils;

import com.cumulocity.model.idtype.GId;

import javax.annotation.Nullable;

public class SimpleTypeUtils {
    public static final String GID_PREFIX = "/inventory/managedObjects/";

    @Nullable
    public static GId parseGId(@Nullable Object property) {
        if (property == null) {
            return null;
        }
        return GId.asGId(property.toString().replace(GID_PREFIX, ""));
    }
}
