package c8y.lora.sdk.service;

import c8y.lora.sdk.rest.BaseCodecRestController;
import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MicroserviceApplication
@RestController
@Import({BaseCodecRestController.class})
public @interface MicroserviceCustomCodecApp {
}
