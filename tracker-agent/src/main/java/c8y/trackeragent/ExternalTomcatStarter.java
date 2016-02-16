package c8y.trackeragent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.cumulocity.agent.server.ServerBuilder;

public class ExternalTomcatStarter extends SpringBootServletInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalTomcatStarter.class);
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        logger.info("Configure tracker agent context for external container.");
        ServerBuilder serverBuilder = Main.serverBuilder();
        serverBuilder.configure(applicationBuilder);
        return applicationBuilder;
    }
}
