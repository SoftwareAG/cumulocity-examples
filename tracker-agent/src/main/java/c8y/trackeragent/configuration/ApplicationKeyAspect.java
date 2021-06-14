package c8y.trackeragent.configuration;

import c8y.trackeragent.tracker.MicroserviceCredentialsFactory;
import c8y.trackeragent.utils.TrackerPlatformProvider;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.RestConnector;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ApplicationKeyAspect {

    @Autowired
    private ContextService<MicroserviceCredentials> contextService;

    @Autowired
    private MicroserviceCredentialsFactory microserviceCredentialsFactory;

    @Autowired
    private TrackerPlatformProvider trackerPlatformProvider;

    @Autowired
    private RestConnector restConnector;

    @Value("${application.key}")
    private String applicationKey;

    @Around("execution(public * com.cumulocity.sdk.client.event.EventApi.*(..))")
//    @Around("execution(public * com.cumulocity.sdk.client.event.EventApi.*(..)) || " +
//            "execution(public * com.cumulocity.sdk.client.alarm.AlarmApi.*(..)) || " +
//            "execution(public * com.cumulocity.sdk.client.measurement.MeasurementApi.*(..))")
    public void applyApplicationKey(ProceedingJoinPoint joinPoint) throws Throwable {
        String tenant = contextService.getContext().getTenant();
        contextService.runWithinContext(microserviceCredentialsFactory.getForTenantWithApplicationKey(tenant, "tracker-agent-application-key"), () -> {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                if ( throwable instanceof RuntimeException) {
                    throw (RuntimeException)throwable;
                } else {
                    throw new RuntimeException(throwable);
                }
            }
        });
    }

//    @Before("execution(public * com.cumulocity.sdk.client.event.EventApi.*(..))")
//    public void beforeApplicationKey() {
//        contextService.getContext().setAppKey(applicationKey);
//    }
//
//    @After("execution(public * com.cumulocity.sdk.client.event.EventApi.*(..))")
//    public void afterApplicationKey() {
//        contextService.getContext().setAppKey(null);
//    }
}
