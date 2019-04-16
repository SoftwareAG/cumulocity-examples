package com.cumulocity.snmp.service.gateway;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.platform.AlarmFactory;
import com.cumulocity.snmp.model.core.Alarms;
import com.cumulocity.snmp.model.gateway.*;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmClearedEvent;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmCreatedEvent;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.cumulocity.snmp.repository.AlarmRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping.c8y_ValidationError;
import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmSeverity.CRITICAL;
import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmSeverity.MAJOR;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientValidationService {
    private final AlarmFactory alarmRepresentationFactory;
    private final AlarmRepository alarmRepository;
    private final Repository<Gateway> gatewayRepository;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @RunWithinContext
    public void storeAlarm(DeviceConfigErrorEvent event) {
        final Gateway gateway = event.getGateway();
        final Device device = event.getDevice();

        final AlarmMapping alarmMapping = AlarmMapping.alarmMapping().type(c8y_ValidationError + "_" + event.getType()).text(event.getMessage()).severity(CRITICAL).build();
        final Optional<AlarmRepresentation> representation = alarmRepresentationFactory.apply(new PlatformRepresentationEvent(DateTime.now(), gateway, device, null, alarmMapping, null));
        if (representation.isPresent()) {
            final Optional<AlarmRepresentation> saved = alarmRepository.create(gateway, representation.get());
            if (saved.isPresent()) {
                gateway.getAlarms().add(event.getType(), saved.get());
                gatewayRepository.save(gateway);
                eventPublisher.publishEvent(new AlarmCreatedEvent(saved.get()));
            }
        }
    }

    @EventListener
    @RunWithinContext
    public void storeAlarm(GatewayConfigErrorEvent event) {
        final Gateway gateway = event.getGateway();
        final Alarms alarms = gateway.getAlarms();

        if (!alarms.existsBySourceAndType(gateway.getId(), event.getType())) {
            final AlarmMapping alarmMapping = AlarmMapping.alarmMapping().type(c8y_ValidationError).text(event.getType().getValue()).severity(CRITICAL).build();
            final Optional<AlarmRepresentation> representation = alarmRepresentationFactory.apply(new PlatformRepresentationEvent(DateTime.now(), gateway, null, null, alarmMapping, null));
            if (representation.isPresent()) {
                final Optional<AlarmRepresentation> saved = alarmRepository.create(gateway, representation.get());
                if (saved.isPresent()) {
                    alarms.add(event.getType(), saved.get());
                    gatewayRepository.save(gateway);
                    eventPublisher.publishEvent(new AlarmCreatedEvent(saved.get()));
                }
            }
        }
    }

    @EventListener
    @RunWithinContext
    public void clearAlarm(DeviceConfigSuccessEvent event) {
        final Gateway gateway = event.getGateway();
        final Device device = event.getDevice();
        final Alarms alarms = gateway.getAlarms();

        for (final AlarmRepresentation alarm : alarms.getBySourceAndType(device.getId(), event.getType())) {
            final Optional<AlarmRepresentation> updated = alarmRepository.clear(event.getGateway(), alarm);
            if (updated.isPresent()) {
                eventPublisher.publishEvent(new AlarmClearedEvent(updated.get()));
            }
        }
        alarms.clearBySourceAndType(device.getId(), event.getType());
        gatewayRepository.save(gateway);
    }

    @EventListener
    @RunWithinContext
    public void clearAlarms(final GatewayConfigSuccessEvent event) {
        final Gateway gateway = event.getGateway();
        final Alarms alarms = gateway.getAlarms();

        for (final AlarmRepresentation alarm : alarms.getBySourceAndType(gateway.getId(), event.getType())) {
            final Optional<AlarmRepresentation> updated = alarmRepository.clear(event.getGateway(), alarm);
            if (updated.isPresent()) {
                eventPublisher.publishEvent(new AlarmClearedEvent(updated.get()));
            }
        }
        alarms.clearBySourceAndType(gateway.getId(), event.getType());
        gatewayRepository.save(gateway);
    }

    @EventListener
    @RunWithinContext
    public void storeAlarms(final UnknownTrapOrDeviceEvent event){
        final Gateway gateway = event.getGateway();
        final Alarms alarms = gateway.getAlarms();

        if (!alarms.existsBySourceAndType(gateway.getId(), event.getType())) {
            final AlarmMapping alarmMapping = AlarmMapping.alarmMapping().type(event.getFragmentType()).text(event.getType().getValue()).severity(MAJOR).build();
            final Optional<AlarmRepresentation> representation = alarmRepresentationFactory.apply(new PlatformRepresentationEvent(DateTime.now(), gateway, null, null, alarmMapping, null));
            if (representation.isPresent()) {
                final Optional<AlarmRepresentation> saved = alarmRepository.create(gateway, representation.get());
                if (saved.isPresent()) {
                    alarms.add(event.getType(), saved.get());
                    gatewayRepository.save(gateway);
                    eventPublisher.publishEvent(new AlarmCreatedEvent(saved.get()));
                }
            }
        }
    }
}
