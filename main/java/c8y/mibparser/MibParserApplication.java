package c8y.mibparser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class MibParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MibParserApplication.class, args);
    }
}
