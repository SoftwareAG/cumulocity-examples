package com.cumulocity.route;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;

@MicroserviceApplication
public class RouteMain {

    public static void main(String... args) {
        SpringApplication.run(RouteMain.class, args);
    }
}
