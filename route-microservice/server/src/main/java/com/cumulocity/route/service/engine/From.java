package com.cumulocity.route.service.engine;

import com.cumulocity.route.model.core.Section;
import lombok.Data;

import java.util.Optional;

public abstract class From<T> implements FilterAndTransform<T> {

    @Data
    private static class PairOfEvents<T> extends From<T> implements FilterAndTransform<T> {

        private final FilterAndTransform beginning;
        private final FilterAndTransform ending;

        @Override
        public Optional apply(Context context, Object event) {
            final Object key = this;

            if (!context.containsFirstEvent(key)) {
                Optional applyBeginning = beginning.apply(context, event);
                if (applyBeginning.isPresent()) {
                    context.putFirstEvent(key, event);
                    return Optional.empty();
                }
            }

            if (context.containsFirstEvent(key)) {
                Optional applyEnding = ending.apply(context, event);
                if (applyEnding.isPresent()) {
                    return Optional.of(new Section<>(context.removeFirstEvent(key), event));
                }
            }

            return Optional.empty();
        }
    }

    public static <T> FilterAndTransform<T> end(Class<T> clazz) {
        return every(clazz);
    }

    public static <T> FilterAndTransform<T> begin(Class<T> clazz) {
        return every(clazz);
    }

    /**
     * Matches all events of selected type.
     */
    public static <T> FilterAndTransform<T> every(Class<T> clazz) {
        return (context, event) -> {
            if (clazz.isInstance(event)) {
                return Optional.of((T) event);
            }
            return Optional.empty();
        };
    }

    /**
     * Matches only first of selected type in context.
     */
    public static <T> FilterAndTransform<T> first(Class<T> clazz) {
        return (context, event) -> context.getFirst(clazz).filter(o -> o.equals(event));
    }

    /**
     * Matches pair of events which satisfies two conditions.
     */
    public static <T, L> From<Section<T, L>> every(FilterAndTransform<T> condition1, FilterAndTransform<L> condition2) {
        return new PairOfEvents<>(condition1, condition2);
    }

    /**
     * If current condition is satisfies then returns last object from context with selected type instead of result of current transformation.
     */
    public <T> FilterAndTransform<T> last(Class<T> clazz) {
        return (context, event) -> {
            final Optional<T> apply = (Optional<T>) From.this.apply(context, event);
            if (apply.isPresent()) {
                return context.getLast(clazz);
            }
            return Optional.empty();
        };
    }
}
