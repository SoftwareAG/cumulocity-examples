package c8y.trackeragent_it.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import c8y.trackeragent_it.TestSettings;

@Configuration
@PropertySource(value = { 
        "file:/etc/tracker-agent/test.properties" })
public class ClientConfiguration {
    
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
    
    
    @Bean
    public TestSettings testSettings() {
        //@formatter:off
        return new TestSettings()
            .setC8yTenant(tenant)
            .setC8yUser(username)
            .setC8yPassword(password)
            .setC8yHost(host)
            .setTrackerAgentHost(trackerAgentHost);
        //@formatter:on            
    }

}
