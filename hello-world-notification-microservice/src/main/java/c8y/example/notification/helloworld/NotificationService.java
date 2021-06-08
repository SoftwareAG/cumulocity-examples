package c8y.example.notification.helloworld;

import c8y.example.notification.helloworld.websocket.NotificationCallback;
// import c8y.example.notification.helloworld.websocket.NotificationConsumerWebSocket;
import c8y.example.notification.helloworld.websocket.NotificationConsumerWebSocket2;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Test class
 */

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationService {

    // TODO TokenApi cannot be injected - investigate
    private final Platform platform;

    @EventListener
    public void onSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) throws InterruptedException, IOException, URISyntaxException {
        log.info("Subscription added for Tenant ID: <{}> ", event.getCredentials().getTenant());
        init();
    }

    public void init() throws URISyntaxException, IOException, InterruptedException {

        final NotificationTokenRequestRepresentation representation = new NotificationTokenRequestRepresentation("sub", "testsubscription", 1440, false);
        //final String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzdWIiLCJ0b3BpYyI6Im1hbmFnZW1lbnQvcmVsbm90aWYvdGVzdHN1YnNjcmlwdGlvbiIsImp0aSI6ImExZGI0MjE0LWJmMmEtNGNmNy04OWJiLTliZDM3YTY1OWVjZCIsImlhdCI6MTYyMzA1MzU5OSwiZXhwIjoxNjI5MDUzNTk5fQ.CKH_Vn9IWKFKzZWkW-Gomck9QblA1pKnxjBbu6nJLIGFF6dsgrXQpSUjJM-HNu2Y3WXcglul-QWJH3oErUwh3l2hPI0pD7XPBlUjinzEfFLtg4-aiYw0Uhkmr1lURqVu3I_qckLxnk_EWR81lc676j8SM28v9Y0TLYA_CUkfwrN-47-BS6dBINTkCsPGl0NfI41VXE8tejCBxZXCEFYhgeqSjmAB6ZMzWAiAaFocpqiT81zJSSnyhIeqos1ZwuOwF_MdE3iud1LJbw2cfZJXM5Nlz1Zt-A9ctoxb1y-qo3QYxgql7ANuUPzYa6Ng2svz2iuYS70Rg9kOXRT4dkiWdA";
        final String token = platform.getTokenApi().create(representation).getTokenString();
        final String webSocketUrl = "ws://localhost:8080/c8y/relnotif/consumer/?token=" + token;
        log.info("TOKEN: " + token);

        NotificationConsumerWebSocket2 socket = new NotificationConsumerWebSocket2(new NotificationCallback() {
            @Override
            public void onNotification(List<String> headers, String notification) {
                for (String header : headers) {
                    log.info("header " + header);
                }
                log.info("notification " + notification);
            }

            @Override
            public void close() {
                log.info("close");
            }
        });

        socket.run(new URI(webSocketUrl), 60, 0);

    }

}
