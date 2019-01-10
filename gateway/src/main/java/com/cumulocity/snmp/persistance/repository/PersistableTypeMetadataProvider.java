package com.cumulocity.snmp.persistance.repository;

import com.cumulocity.snmp.annotation.core.PersistableType;
import com.cumulocity.snmp.persistance.model.PersistableTypeMetadata;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.FluentIterable.from;

@Component
class PersistableTypeMetadataProvider {

    @Autowired
    ApplicationContext appContext;

    private final List<PersistableTypeMetadata> metadataList = new ArrayList<>();

    @PostConstruct
    private void findAnnotatedClasses() {
        final Map<String, Object> beansWithAnnotation = appContext.getBeansWithAnnotation(PersistableType.class);
        for (Object persistableType : beansWithAnnotation.values()) {
            createMetadata(persistableType.getClass());
        }
    }

    public Class getPersistableClass(final String typeName, final String discriminator) {
        return from(metadataList).firstMatch(new Predicate<PersistableTypeMetadata>() {
            public boolean apply(final PersistableTypeMetadata metadata) {
                return StringUtils.equals(typeName, metadata.getTypeName()) && StringUtils.equals(discriminator, metadata.getDiscriminator());
            }
        }).transform(new Function<PersistableTypeMetadata, Class>() {
            public Class apply(final PersistableTypeMetadata metadata) {
                return metadata.getPersistableClass();
            }
        }).or(new Supplier<Class>() {
            public Class get() {
                throw new IllegalStateException("Type '" + typeName + "' if not persistable, missing @PersistableType annotation");
            }
        });
    }

    public String getTypeName(final Class persistableClass) {
        return getTypeMetadata(persistableClass).getTypeName();
    }

    public String getDiscriminator(final Class persistableClass) {
        return getTypeMetadata(persistableClass).getDiscriminator();
    }

    public String getStoreName(Class clazz) {
        return getTypeMetadata(clazz).getStoreName();
    }

    public boolean isAutowire(Class clazz) {
        return getTypeMetadata(clazz).isAutowire();
    }

    public PersistableTypeMetadata getTypeMetadata(final Class persistableClass) {
        return from(metadataList).firstMatch(new Predicate<PersistableTypeMetadata>() {
            @Override
            public boolean apply(final PersistableTypeMetadata metadata) {
                return metadata.getPersistableClass().equals(persistableClass);
            }
        }).or(new Supplier<PersistableTypeMetadata>() {
            @Override
            public PersistableTypeMetadata get() {
                return createMetadata(persistableClass).or(new Supplier<PersistableTypeMetadata>() {
                    @Override
                    public PersistableTypeMetadata get() {
                        throw new IllegalStateException("Type '" + persistableClass + "' is not persistable, missing @PersistableType annotation");
                    }
                });
            }
        });
    }

    private Optional<PersistableTypeMetadata> createMetadata(Class persistableClass) {
        final PersistableType annotation = AnnotationUtils.findAnnotation(persistableClass, PersistableType.class);
        if (annotation != null) {
            final PersistableTypeMetadata result = new PersistableTypeMetadata(
                    annotation.value(),
                    annotation.discriminator(),
                    persistableClass,
                    annotation.autowire(),
                    annotation.inMemory(),
                    annotation.runWithinContext()
            );
            metadataList.add(result);
            return of(result);
        }
        return absent();
    }
}
