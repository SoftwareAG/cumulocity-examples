package com.cumulocity.snmp.configuration.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SNMPConfigurationProperties {

    @Value("${snmp.trapListener.address:localhost}")
    private String address;

    @Value("${snmp.trapListener.port:161}")
    private int listenerPort;

    @Value("${snmp.trapListener.threadPoolSize:10}")
    private short threadPoolSize;

    @Value("${snmp.community.target:public}")
    private String communityTarget;

    @Value("${snmp.polling.port:161}")
    private int pollingPort;
}
