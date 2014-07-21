package c8y.inject;

import static com.cumulocity.agent.server.context.Scopes.DEVICE_SCOPE;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

import java.lang.annotation.*;

import org.springframework.context.annotation.Scope;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = DEVICE_SCOPE, proxyMode = TARGET_CLASS)
public @interface DeviceScope {
    String value() default "";
}
