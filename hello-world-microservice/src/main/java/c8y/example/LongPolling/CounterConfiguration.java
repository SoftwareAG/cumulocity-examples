package c8y.example.LongPolling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class CounterConfiguration {
    @Bean
    public AtomicInteger deviceCounter() {
        return new AtomicInteger(0);
    }
}
