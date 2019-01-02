package c8y.mibparser.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {
    @Value("${application.name}")
    private String applicationName;

    @Value("${C8Y.baseURL}")
    private String baseUrl;

    @Value("${C8Y.forceInitialHost:false}")
    private boolean forceInitialHost;

    @Value("${C8Y.bootstrap.tenant}")
    private String tenant;

    @Value("${C8Y.bootstrap.user}")
    private String user;

    @Value("${C8Y.bootstrap.password}")
    private String password;

    @Bean
    public Properties properties() {
        return Properties.builder()
                .applicationName(applicationName)
                .baseUrl(baseUrl)
                .forceInitialHost(forceInitialHost)
                .tenant(tenant)
                .user(user)
                .password(password)
                .build();
    }
}
