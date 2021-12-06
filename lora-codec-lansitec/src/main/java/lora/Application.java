package lora;

import com.cumulocity.microservice.lpwan.codec.annotation.CodecMicroserviceApplication;
import org.springframework.boot.SpringApplication;

@CodecMicroserviceApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}