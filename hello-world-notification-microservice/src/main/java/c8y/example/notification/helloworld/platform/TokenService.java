package c8y.example.notification.helloworld.platform;

import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.Token;
import com.cumulocity.sdk.client.messaging.notifications.TokenApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenService {

    private final TokenApi tokenApi;

    public String create(NotificationTokenRequestRepresentation tokenRequestRepresentation) {
        return tokenApi.create(tokenRequestRepresentation).getTokenString();
    }

    public String refresh(String expiredToken) {
        return tokenApi.refresh(new Token(expiredToken)).getTokenString();
    }
}
