package com.cumulocity.snmp.annotation.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface PersistableType {

    String value();

    String discriminator() default "";

    boolean autowire() default false;

    boolean inMemory() default false;

    Class<?> runWithinContext() default Object.class;
}
