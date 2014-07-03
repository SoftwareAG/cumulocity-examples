package com.cumulocity.agent.server.config;

import static com.cumulocity.agent.server.context.DeviceContextScope.CONTEXT_SCOPE;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.cumulocity.agent.server.annotation.DeviceContextScope;
import com.cumulocity.agent.server.context.CumulocityClientCache;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.audit.AuditRecordApi;
import com.cumulocity.sdk.client.cep.CepApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

@Configuration
public class CumulocityClientConfiguration {

    public static final String C8Y_HOST_PROP = "C8Y.baseURL";

    public static final String C8Y_PROXY_PROP = "C8Y.proxy";

    public static final String C8Y_PROXY_PORT_PROP = "C8Y.proxyPort";

    @Value("${" + C8Y_HOST_PROP + "}")
    private String host;

    @Value("${" + C8Y_PROXY_PROP + ":}")
    private String proxy;

    @Value("${" + C8Y_PROXY_PORT_PROP + ":0}")
    private Integer proxyPort;

    @Bean
    @Autowired
    public CumulocityClientCache cumulocityClientCache() {
        return new CumulocityClientCache(host, proxy, proxyPort);
    }

    @Bean
    @Autowired
    @DeviceContextScope
    public CumulocityClientFactoryBean cumulocityClient(DeviceContextService contextService) {
        return new CumulocityClientFactoryBean(contextService, cumulocityClientCache());
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public InventoryApi inventoryApi(Platform platform) throws SDKException {
        return platform.getInventoryApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public DeviceCredentialsApi deviceCredentialsApi(Platform platform) throws SDKException {
        return platform.getDeviceCredentialsApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public EventApi eventApi(Platform platform) throws SDKException {
        return platform.getEventApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public MeasurementApi measurementApi(Platform platform) throws SDKException {
        return platform.getMeasurementApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public IdentityApi identityApi(Platform platform) throws SDKException {
        return platform.getIdentityApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public AlarmApi alarmApi(Platform platform) throws SDKException {
        return platform.getAlarmApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public AuditRecordApi auditRecordApi(Platform platform) throws SDKException {
        return platform.getAuditRecordApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public DeviceControlApi deviceControlApi(Platform platform) throws SDKException {
        return platform.getDeviceControlApi();
    }

    @Bean
    @Autowired
    @DeviceContextScope
    @Lazy
    public CepApi cepApi(Platform platform) throws SDKException {
        return platform.getCepApi();
    }

    public static class CumulocityClientFactoryBean implements FactoryBean<PlatformImpl> {

        private final DeviceContextService contextService;

        private final CumulocityClientCache cache;

        public CumulocityClientFactoryBean(DeviceContextService contextService, CumulocityClientCache cache) {
            this.contextService = contextService;
            this.cache = cache;
        }

        @Override
        public PlatformImpl getObject() throws Exception {
            DeviceCredentials login = contextService.getCumulocityLogin();
            return cache.get(login);
        }

        @Override
        public Class<?> getObjectType() {
            return PlatformImpl.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

    }

}
