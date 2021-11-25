package c8y.example;

import com.cumulocity.lpwan.codec.annotation.CodecMicroserviceApplication;
import org.springframework.boot.SpringApplication;

@CodecMicroserviceApplication
public class App {
    public static void main (String[] args) {
        SpringApplication.run(App.class, args);
    }
}
