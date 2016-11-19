package c8y.trackeragent.sms;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class OptionsAuthorizationInterceptor implements ClientHttpRequestInterceptor {

    private final OptionsAuthorizationSupplier optionsAuth;

    public OptionsAuthorizationInterceptor(OptionsAuthorizationSupplier optionsAuth) {
        this.optionsAuth = optionsAuth;
    }
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final HttpHeaders headers = request.getHeaders();
        headers.set("Authorization", optionsAuth.getAuth());
        return execution.execute(request, body);
};
    
}
