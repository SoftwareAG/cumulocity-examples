package com.cumulocity.snmp.repository.configuration;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.reverse;

public class ContextProvider {
    public interface Callable<T> {
        T call() throws Throwable;
    }

    private static ThreadLocal<LinkedList<Object>> context = new ThreadLocal<LinkedList<Object>>() {
        @Override
        protected LinkedList<Object> initialValue() {
            return new LinkedList<>();
        }
    };

    public static <T> T doInvoke(Object context, Callable<T> runnable) throws Throwable {
        try {
            enter(context);
            return runnable.call();
        } finally {
            leave();
        }
    }

    public static void doInvoke(Object context, Runnable runnable) {
        try {
            enter(context);
            runnable.run();
        } finally {
            leave();
        }
    }

    private static void enter(Object context) {
        ContextProvider.context.get().add(context);
    }

    private static void leave() {
        context.get().removeLast();
    }

    public static <T> Optional<T> get(final Class<T> clazz) {
        final LinkedList<Object> list = context.get();
        final List<Object> reverse = reverse(list);
        return from(reverse).firstMatch(new Predicate<Object>() {
            public boolean apply(Object o) {
                return clazz.isInstance(o);
            }
        }).transform(new Function<Object, T>() {
            public T apply(Object o) {
                return (T) o;
            }
        });
    }
}
