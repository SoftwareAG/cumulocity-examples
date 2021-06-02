package c8y.trackeragent_it.config;

import c8y.trackeragent.service.AlarmMappingService;
import c8y.trackeragent.service.AlarmMappingServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import c8y.trackeragent_it.TestSettings;

@Configuration
@PropertySource(value = { "classpath:tracker-agent-server.properties" })
public class TestConfiguration {
    
    @Value("${C8Y.tenant}")
    private String tenant;

    @Value("${C8Y.username}")
    private String username;

    @Value("${C8Y.password}")
    private String password;
    
    @Value("${C8Y.host}")
    private String host;

    @Value("${tracker-agent.host}")
    private String trackerAgentHost;
    
    @Value("${C8Y.devicebootstrap.user}")
    private String bootstrapUser;
    
    @Value("${C8Y.devicebootstrap.password}")
    private String bootstrapPassword;
    
    @Bean
    public TestSettings testSettings() {
        //@formatter:off
        TestSettings result = new TestSettings()
            .setC8yTenant(tenant)
            .setC8yUser(username)
            .setC8yPassword(password)
            .setC8yHost(host)
            .setTrackerAgentHost(trackerAgentHost)
            .setBootstrapUser(bootstrapUser)
            .setBootstrapPassword(bootstrapPassword);
        return result;
        //@formatter:on            
    }

    @Bean
    public AlarmMappingService alarmMappingService() {
        return new AlarmMappingServiceImpl("classpath:alarm-configuration");
    }
}
