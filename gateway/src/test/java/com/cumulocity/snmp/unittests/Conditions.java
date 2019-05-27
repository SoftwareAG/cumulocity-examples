package com.cumulocity.snmp.unittests;

import com.google.common.base.Optional;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Condition;

@UtilityClass
public class Conditions {
    public static Condition<Optional> present() {
        return new Condition<Optional>() {
            @Override
            public boolean matches(Optional optional) {
                return optional.isPresent();
            }
        };
    }

    public static <E> Condition<Optional<E>> equalTo(final E e) {
        return new Condition<Optional<E>>() {
            @Override
            public boolean matches(Optional<E> optional) {
                return optional.get().equals(e);
            }
        };
    }
}
