package c8y.trackeragent.sms;

import c8y.trackeragent.configuration.TrackerConfiguration;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.springframework.stereotype.Component;

import com.google.common.io.BaseEncoding;

@Component
public class OptionsAuthorizationSupplier {
    
    private final ThreadLocal<String> threadLocal = new ThreadLocal<String>();
    
    public void optionsAuthForTenant(TrackerConfiguration configuration, String tenant) {
        String optionsReaderUser = configuration.getOptionsReaderUser();
        String password = configuration.getOptionsReaderPassword();
        
        String authString = "Basic ";
        String authentication = checkTenant(tenant) + optionsReaderUser + ":" + password;
        authString += new String(BaseEncoding.base64().encode(authentication.getBytes()));
        
        threadLocal.set(authString);
    }

    public String getAuth() {
        return threadLocal.get();
    }
    
    private String checkTenant(String tenant) {
        return isNullOrEmpty(tenant) ? "" : tenant + "/";
    }

}
