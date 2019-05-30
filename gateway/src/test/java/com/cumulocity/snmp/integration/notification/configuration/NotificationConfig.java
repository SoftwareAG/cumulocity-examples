package com.cumulocity.snmp.integration.notification.configuration;

import com.cumulocity.snmp.integration.notification.BayeuxRealtimeBroadcaster;
import com.cumulocity.snmp.integration.notification.NotificationBayeuxServerImpl;
import com.cumulocity.snmp.integration.notification.RealtimeBroadcaster;
import com.cumulocity.snmp.integration.notification.controller.CepRealtimeController;
import com.cumulocity.snmp.integration.notification.controller.DeviceControllNotificationController;
import org.cometd.server.BayeuxServerImpl;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public BayeuxServerImpl realtimeServer() {
        return new NotificationBayeuxServerImpl();
    }

    @Bean
    public RealtimeBroadcaster realtimeBroadcaster(final BayeuxServerImpl realtimeServer) {
        return new BayeuxRealtimeBroadcaster(realtimeServer);
    }

    @Bean
    public ServletRegistrationBean cepRealtime(final BayeuxServerImpl realtimeServer) {
        return new ServletRegistrationBean(new CepRealtimeController(realtimeServer), "/cep/realtime");
    }

    @Bean
    public ServletRegistrationBean devicecontrolNotifications(final BayeuxServerImpl realtimeServer) {
        return new ServletRegistrationBean(new DeviceControllNotificationController(realtimeServer), "/devicecontrol/notifications");
    }
}
