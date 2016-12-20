package c8y.trackeragent.sms;

import c8y.trackeragent.configuration.TrackerConfiguration;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.springframework.stereotype.Component;

import com.google.common.io.BaseEncoding;

@Component
public class OptionsAuthorizationSupplier {

    private final ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public void optionsAuthForTenant(TrackerConfiguration configuration, String tenant) {
        String auth = formatAuth(tenant, configuration.getSmsGatewayUser(), configuration.getSmsGatewayPassword());
        auth = "Basic " + new String(BaseEncoding.base64().encode(auth.getBytes()));
        threadLocal.set(auth);
    }

    private static String formatAuth(String tenant, String username, String password) {
        if (isNullOrEmpty(tenant)) {
            return String.format("%s:%s", username, password);
        } else {
            return String.format("%s/%s:%s", tenant, username, password);
        }
    }

    public String getAuth() {
        return threadLocal.get();
    }
}
