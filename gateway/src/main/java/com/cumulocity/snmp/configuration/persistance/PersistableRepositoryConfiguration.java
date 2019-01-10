package com.cumulocity.snmp.configuration.persistance;

import com.cumulocity.snmp.persistance.repository.PersistableRepository;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.ParameterizedType;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
public class PersistableRepositoryConfiguration {
    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public PersistableRepository repository(final InjectionPoint injectionPoint) {
        final ParameterizedType genericParameterType = getParameterizedType(injectionPoint);
        final Class persistable = (Class) genericParameterType.getActualTypeArguments()[0];
        return new PersistableRepository<>(persistable);
    }

    private ParameterizedType getParameterizedType(InjectionPoint injectionPoint) {
        if (injectionPoint.getMethodParameter() != null) {
            return (ParameterizedType) injectionPoint.getMethodParameter().getGenericParameterType();
        } else {
            return (ParameterizedType) injectionPoint.getField().getGenericType();
        }
    }
}
