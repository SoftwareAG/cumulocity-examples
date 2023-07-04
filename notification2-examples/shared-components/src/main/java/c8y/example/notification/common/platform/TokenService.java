package c8y.example.notification.common.platform;

import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.Token;
import com.cumulocity.sdk.client.messaging.notifications.TokenApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenService {

    private final TokenApi tokenApi;

    public String create(NotificationTokenRequestRepresentation tokenRequestRepresentation) {
        return tokenApi.create(tokenRequestRepresentation).getTokenString();
    }

    public String refresh(String expiredToken) {
        return tokenApi.refresh(new Token(expiredToken)).getTokenString();
    }

    public void unsubscribe(String token) {
        tokenApi.unsubscribe(new Token(token));
    }

}
