package c8y.trackeragent_it;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import c8y.trackeragent.Main;
import c8y.trackeragent.Server;
import c8y.trackeragent.ServerFactory;
import c8y.trackeragent.devicebootstrap.DeviceBinder;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.agent.server.feature.ContextFeature;
import com.cumulocity.agent.server.feature.RepositoryFeature;
import com.cumulocity.agent.server.logging.LoggingService;
import com.cumulocity.agent.server.repository.BinariesRepository;
import com.cumulocity.agent.server.repository.DeviceControlRepository;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"c8y.trackeragent"},excludeFilters={
  @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Main.class)
})
@Import({RepositoryFeature.class, ContextFeature.class})
@PropertySource(value = { "file:/etc/tracker-agent/test.properties", 
        "file:/etc/tracker-agent/tracker-agent-server.properties" })
public class ITConfiguration {

    @Autowired
    ServerFactory serverFactory;
    
    @Autowired
    TrackerConfiguration config;
    
    @Autowired
    DeviceBinder deviceBinder;
    
    @Bean
    @Autowired
    public LoggingService loggingService(DeviceControlRepository deviceControl, BinariesRepository binaries, 
            @Value("${C8Y.log.file.path}") String logfile, @Value("${C8Y.log.timestamp.format:}") String timestampFormat,
            @Value("${C8Y.application.id}") String applicationId) {
        return new LoggingService(deviceControl, binaries, logfile, timestampFormat, applicationId);
    }
    
    @PostConstruct
    public void startServer() {
        startServer(config.getLocalPort1());
        startServer(config.getLocalPort2());
        deviceBinder.init();
    }
    
    private void startServer(int localPort) {
        Server server = serverFactory.createServer(localPort);
        server.init();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(server);
    }
}
