package c8y.mibparser;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Slf4j
@MicroserviceApplication
@EnableAutoConfiguration
@PropertySources(value = {
        @PropertySource(value = "file:${user.home}/.mibparser/mibparser.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${mibparser.conf.dir:/etc}/mibparser/mibparser.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:META-INF/spring/mibparser.properties", ignoreResourceNotFound = true)
})
public class MibParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MibParserApplication.class, args);
    }
}
