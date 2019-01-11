package com.cumulocity.snmp.persistance.model;

import com.cumulocity.snmp.model.core.TenantProvider;
import com.cumulocity.snmp.repository.configuration.ContextProvider;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import lombok.Data;
import lombok.NonNull;


@Data
public class PersistableTypeMetadata {
    @NonNull
    private final String typeName;
    private final String discriminator;
    @NonNull
    private final Class persistableClass;
    private final boolean autowire;
    private final boolean inMemory;
    private final Class<?> context;

    public String getStoreName() {
        if (TenantProvider.class.isAssignableFrom(context)) {
            return ContextProvider.get(TenantProvider.class).transform(new Function<TenantProvider, String>() {
                public String apply(TenantProvider context) {
                    return context.getTenant() + "_" + getTypeName();
                }
            }).or(new Supplier<String>() {
                @Override
                public String get() {
                    throw new IllegalStateException("Should run in context.");
                }
            });
        } else {
            return getTypeName();
        }
    }
}
