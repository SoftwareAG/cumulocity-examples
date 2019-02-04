package com.cumulocity.snmp.utils;

import com.cumulocity.model.idtype.GId;

import javax.annotation.Nullable;

public class SimpleTypeUtils {
    public static final String GID_PREFIX = "/inventory/managedObjects/";

    @Nullable
    public static String parseString(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    @Nullable
    public static Long parseLong(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        final String string = object.toString();
        if (string.matches("^[-+]?\\d+$")) {
            return Long.parseLong(string);
        }
        return null;
    }

    @Nullable
    public static Integer parseInt(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        final String string = object.toString();
        if (string.matches("^[-+]?\\d+$")) {
            return Integer.parseInt(string);
        }
        return null;
    }

    @Nullable
    public static GId parseGId(@Nullable Object property) {
        if (property == null) {
            return null;
        }
        return GId.asGId(property.toString().replace(GID_PREFIX, ""));
    }
}
