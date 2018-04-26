package com.cumulocity.route.service.engine;

import com.cumulocity.microservice.context.inject.TenantScope;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@TenantScope
@SuppressWarnings("unchecked")
public class StateMachine {

    private List<FilterAndTransform> rules = Lists.newArrayList();
    private List<FilterAndTransform> handlers = Lists.newArrayList();
    private List<FilterAndTransform> partitions = Lists.newArrayList();
    private Map<Object, Context> contexts = Maps.newHashMap();

    /**
     * Every event will be run against the rule. Result of the rule will be queued as new event.
     */
    public <L> void insert(FilterAndTransform rule) {
        rules.add(rule);
    }

    /**
     * Every event will be run against the rule. Result of the rule will be ignored.
     */
    public <L> void handle(FilterAndTransform<L> rule) {
        handlers.add(rule);
    }

    /**
     * Context partition is used for saving state of events
     */
    public void contextPartition(FilterAndTransform partition) {
        partitions.add(partition);
    }

    /**
     * Run event for every handler and every rule in context.
     */
    public void send(Object event) {
        final Context context = findContext(event);
        context.setCurrent(event);

        handlers.forEach(o -> {
            o.apply(context, event);
        });

        final List<Object> queue = Lists.newLinkedList();
        try {
            rules.forEach(o -> {
                o.apply(context, event)
                        .filter(o1 -> {
                            if (o1.equals(event)) {
//                                this is very simple prevention against loops working only when you do something like service.insert(every(Model.class))
                                log.warn("Introduced infinite loop: {}", event);
                                return false;
                            }
                            return true;
                        })
                        .ifPresent(queue::add);
            });
        } finally {
            queue.forEach(this::send);
        }
    }

    @SneakyThrows
    private Context findContext(Object event) {
        for (final FilterAndTransform partition : partitions) {
            final Optional<Object> partitionKey = partition.apply(null, event);
            final Optional<Context> maybeContext = partitionKey.map(o -> contexts.computeIfAbsent(o, Context::new));
            if (maybeContext.isPresent()) {
                return maybeContext.get();
            }
        }
        throw new IllegalStateException("Not in context " + event);
    }
}
