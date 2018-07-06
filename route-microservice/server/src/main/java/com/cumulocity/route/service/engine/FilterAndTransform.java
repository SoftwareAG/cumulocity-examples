package com.cumulocity.route.service.engine;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface FilterAndTransform<T> {

    default FilterAndTransform<T> filter(Predicate<T> where) {
        return (context, event) -> FilterAndTransform.this.apply(context, event).filter(where);
    }

    default <L> FilterAndTransform<L> transform(Function<T, L> transform) {
        return (context, event) -> FilterAndTransform.this.apply(context, event).map(transform);
    }

    default FilterAndTransform<T> consume(Consumer<T> transform) {
        return transform(t -> {
            transform.accept(t);
            return null;
        });
    }

    Optional<T> apply(Context context, Object event);
}
