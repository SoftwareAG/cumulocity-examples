package com.cumulocity.helloworld;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@MicroserviceApplication
@RestController
public class HelloWorldMain {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMain.class, args);
    }

    @RequestMapping("hello")
    public String greeting(@RequestParam(value = "who", defaultValue = "world") String who) {
        return "hello " + who + "!";
    }

    @Bean
    public MicroserviceMetadataRepresentation metadata() {
        return MicroserviceMetadataRepresentation.microserviceMetadataRepresentation().build();
    }
}
