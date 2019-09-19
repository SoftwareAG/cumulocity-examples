package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.config.PlatformProvider;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * A service to provide access to Platform objects and its exposed API classes.
 * This service also initializes the Platform object when the device credentials are available,
 * monitors and maintains the Platform status (available/unavailable).
 */

@Service
@Slf4j
public class SnmpAgentGatewayService {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private GatewayProperties gatewayProperties;

    @Autowired
    private PlatformProvider platformProvider;


    private volatile boolean isPlatformAvailable = false;

    @PostConstruct
    private void startPlatformAvailabilityMonitor() {
        taskScheduler.scheduleWithFixedDelay(() -> {
            if (!isPlatformAvailable()) {
                if(checkPlatformAvailability()) {
                    markPlatfromAsAvailable();
                }
                else {
                    log.info("Platform is unavailable. Waiting for {} seconds before retry.", gatewayProperties.getBootstrapFixedDelay()/1000);
                }
            }
        }, gatewayProperties.getBootstrapFixedDelay());
    }

    public boolean isPlatformAvailable() {
        return isPlatformAvailable;
    }

    public void markPlatfromAsUnavailable() {
        isPlatformAvailable = false;
    }

    public void markPlatfromAsAvailable() {
        isPlatformAvailable = true;

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void waitForPlatformToBeAvailable() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    @Bean(name = "platformWithBootstrapCredentials")
    public Platform accessPlatformWithBootstrapCredentials() {
        CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                .tenantId(gatewayProperties.getBootstrapProperties().getTenantId())
                .username(gatewayProperties.getBootstrapProperties().getUsername())
                .password(gatewayProperties.getBootstrapProperties().getPassword())
                .build();

        return PlatformBuilder.platform()
                .withBaseUrl(gatewayProperties.getBaseUrl())
                .withForceInitialHost(gatewayProperties.isForceInitialHost())
                .withCredentials(credentials)
                .build();
    }

    private boolean checkPlatformAvailability() {
        try {
            platformProvider.getPlatform().getMeasurementApi();
        } catch(Throwable t) {
            log.debug("Platform is unavailable or the device credentials are incorrect.", t);
            return false;
        }

        return true;
    }
}
