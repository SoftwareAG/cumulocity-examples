package com.cumulocity.snmp.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.core.MeasurementUnit;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayConfigErrorEvent;
import com.cumulocity.snmp.repository.configuration.ContextProvider;
import com.google.common.base.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroupMeasurementService {

    private final Map<GId, ScheduledFuture> executors = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Getter(AccessLevel.PACKAGE)
    private final List<MeasurementUnit> measurementUnits = new CopyOnWriteArrayList<>();
    private final Executor worker;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final ApplicationEventPublisher eventPublisher;

    @RunWithinContext
    public void queueForExecution(final Credentials credentials, final MeasurementUnit measurementUnit) {
        final Optional<Gateway> gatewayOptional = ContextProvider.get(Gateway.class);

        if (gatewayOptional.isPresent()) {
            final Gateway gateway = gatewayOptional.get();
            if (gateway.getTransmitRateInSeconds() == null) {
                executeInDifferentThead(gateway, measurementUnit);
            } else {
                synchronized (this) {
                    measurementUnits.add(measurementUnit);

                    if (!executors.containsKey(gateway.getId())) {
                        executors.put(gateway.getId(), scheduler.schedule(new Runnable() {
                            public void run() {
                                executePendingCommands(gateway);
                            }
                        }, gateway.getTransmitRateInSeconds(), TimeUnit.SECONDS));
                    }
                }
            }
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        try {
            for (ScheduledFuture executor : executors.values()) {
                executor.cancel(true);
            }
        } finally {
            executors.clear();
        }
    }

    @RunWithinContext
    private void executePendingCommands(final Gateway gateway) {
        final List<MeasurementUnit> localList;
        synchronized (this) {
            localList = new ArrayList<>(measurementUnits);
            measurementUnits.clear();
            executors.remove(gateway.getId());
        }

        for (final MeasurementUnit measurementUnit : localList) {
            executeInDifferentThead(gateway, measurementUnit);
        }
    }

    private void executeInDifferentThead(final Gateway gateway, final MeasurementUnit measurementUnit) {
        worker.execute(new Runnable() {
            public void run() {
                execute(gateway, measurementUnit);
            }
        });
    }

    @RunWithinContext
    private void execute(final Gateway gateway, MeasurementUnit measurementUnit) {
        try {
            autowireCapableBeanFactory.autowireBean(measurementUnit);
            measurementUnit.execute();
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            eventPublisher.publishEvent(new GatewayConfigErrorEvent(gateway, new ConfigEventType(ex.getMessage())));
        }
    }
}
