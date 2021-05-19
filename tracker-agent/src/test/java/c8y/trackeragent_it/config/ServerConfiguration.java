package c8y.trackeragent_it.config;

import c8y.trackeragent.Main;
import c8y.trackeragent.devicebootstrap.TenantBinder;
import c8y.trackeragent.operations.BinariesRepository;
import c8y.trackeragent.operations.DeviceControlRepository;
import c8y.trackeragent.operations.LoggingService;
import c8y.trackeragent.server.Servers;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sms.client.SmsMessagingApi;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "c8y.trackeragent" }, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Main.class) })
//@Import({RepositoryFeature.class, ContextFeature.class})
@PropertySource(value = { "file:/etc/tracker-agent/tracker-agent-server.properties" })
public class ServerConfiguration {

    @Autowired
    Servers servers;
    
    @Autowired
    TenantBinder tenantBinder;

    @Autowired
    DeviceControlApi deviceControlApi;
    
    @Bean
    @Autowired
    public LoggingService loggingService(DeviceControlRepository deviceControl, BinariesRepository binaries, DeviceControlApi deviceControlApi,
                                         @Value("${C8Y.log.file.path}") String logfile, @Value("${C8Y.log.timestamp.format:}") String timestampFormat,
                                         @Value("${C8Y.application.id}") String applicationId) {
        return new LoggingService(deviceControl, binaries, deviceControlApi, logfile, timestampFormat, applicationId);
    }
    
    @Bean
    public SmsMessagingApi outgoingMessagingClient() {
        return Mockito.mock(SmsMessagingApi.class);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @PostConstruct
    public void startServer() throws IOException {
        servers.startAll();
        tenantBinder.init();
    }
}
