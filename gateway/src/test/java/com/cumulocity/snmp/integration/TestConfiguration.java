package com.cumulocity.snmp.integration;

import com.cumulocity.sdk.client.Platform;
import com.cumulocity.snmp.persistance.repository.DBStore;
import com.cumulocity.snmp.repository.platform.PlatformProperties;
import com.cumulocity.snmp.repository.platform.PlatformProvider;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Configuration
@SpringBootTest
@WebAppConfiguration
@ComponentScan("com.cumulocity.snmp.integration")
public class TestConfiguration extends WebMvcConfigurerAdapter implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @Value("${C8Y.bootstrap.tenant:management}")
    private String tenant;

    @Value("${C8Y.bootstrap.user:devicebootstrap}")
    private String user;

    @Value("${C8Y.bootstrap.password:Fhdt1bb1f}")
    private String password;

    private int port;

    @Override
    public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {
        port = event.getEmbeddedServletContainer().getPort();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new SvensonConverter());
    }

    @Bean
    @Lazy
    @Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = TARGET_CLASS)
    public PlatformProperties platformProperties() {
        return PlatformProperties.builder()
                .url("http://localhost:" + port)
                .forceInitialHost(false)
                .bootstrap(PlatformProperties.DeviceBootstrapConfigurationProperties.builder()
                        .tenant(tenant)
                        .user(user)
                        .password(password)
                        .build())
                .build();
    }

    @Bean
    @Lazy
    @Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = TARGET_CLASS)
    public PlatformProvider platformProvider(final PlatformProperties platformProperties) {
        return new PlatformProvider(platformProperties);
    }

    @Bean
    @Lazy
    @Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = TARGET_CLASS)
    public Platform bootstrapPlatform(PlatformProvider platformProvider) throws ExecutionException {
        return platformProvider.getBootstrapPlatform();
    }

    @Primary
    @Bean(destroyMethod = "close")
    public DBStore db() {
        return new DBStore() {
            protected DB db() {
                return DBMaker.memoryDB().make();
            }
        };
    }

}
