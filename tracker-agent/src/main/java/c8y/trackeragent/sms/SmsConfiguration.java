package c8y.trackeragent.sms;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SmsConfiguration {

    @Autowired
    RestTemplate template;
    @Autowired
    OptionsAuthorizationSupplier optionsAuth;
    
    @PostConstruct
    public void initialize() {
        OptionsAuthorizationInterceptor optionsAuthorizationInterceptor = new OptionsAuthorizationInterceptor(optionsAuth);
        template.getInterceptors().add(optionsAuthorizationInterceptor);
    }
    
}
