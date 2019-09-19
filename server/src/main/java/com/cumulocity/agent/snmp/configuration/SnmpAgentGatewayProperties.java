package com.cumulocity.agent.snmp.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SnmpAgentGatewayProperties {

    @Value("#{'${gateway.identifier:snmp-agent}'.trim()}")
    private String gatewayIdentifier;

    @Value("#{'${C8Y.baseURL:http://developers.cumulocity.com}'.trim()}")
    private String url;

    @Value("#{'${C8Y.bootstrap.tenant:management}'.trim()}")
    private String tenant;

    @Value("#{'${C8Y.bootstrap.user:devicebootstrap}'.trim()}")
    private String user;

    @Value("#{'${C8Y.bootstrap.password:}'.trim()}")
    private String password;

    @Value("#{'${C8Y.forceInitialHost:false}'.trim()}")
    private boolean forceInitialHost;

    @Value("#{'${snmp.trapListener.threadPoolSize:10}'.trim()}")
    private int threadPoolSize;

    public int getMeasurementThreadPoolSize() {
        return threadPoolSize * 30/100; // 10% of the total threads available
    }

    public int getAlarmThreadPoolSize() {
        return threadPoolSize * 10/100; // 10% of the total threads available
    }

    public int getEventThreadPoolSize() {
        return threadPoolSize * 10/100; // 10% of the total threads available
    }
}

