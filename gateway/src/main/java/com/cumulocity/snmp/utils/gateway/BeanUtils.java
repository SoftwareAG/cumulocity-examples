package com.cumulocity.snmp.utils.gateway;

import org.springframework.aop.TargetClassAware;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public class BeanUtils {

    public static  <T, S> T findBeanByGenericType(ApplicationContext applicationContext, Class<T> beanClazz, Class<S> paramClazz) {
        final Map<String, T> beansOfType = applicationContext.getBeansOfType(beanClazz);
        for (final T bean : beansOfType.values()) {
            final ParameterizedType thisType = (ParameterizedType) findTargetClass(bean).getGenericInterfaces()[0];
            if (thisType != null) {
                final Class<?> parametrizedClass = (Class<?>) thisType.getActualTypeArguments()[0];
                if (parametrizedClass.isAssignableFrom(paramClazz)) {
                    return bean;
                }
            }
        }
        throw new IllegalStateException("'Bean not found'" + beanClazz.getSimpleName() + "' with generic type '" + paramClazz.getSimpleName() +"'");
    }

    private static <T> Class<?> findTargetClass(T bean) {
        if (bean instanceof TargetClassAware) {
            return ((TargetClassAware) bean).getTargetClass();
        }
        return bean.getClass();
    }
}
