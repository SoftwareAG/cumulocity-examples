package c8y.remoteaccess.tunnel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEndpointConfig implements javax.websocket.ClientEndpointConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClientEndpointConfig.class);

    private String credentials;

    public ClientEndpointConfig(String tenant, String username, String password) {
        this.credentials = tenant + "/" + username + ":" + password;
    }

    @Override
    public List<Class<? extends Encoder>> getEncoders() {
        return new ArrayList<Class<? extends Encoder>>();
    }

    @Override
    public List<Class<? extends Decoder>> getDecoders() {
        return new ArrayList<Class<? extends Decoder>>();
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return new HashMap<String, Object>();
    }

    @Override
    public List<String> getPreferredSubprotocols() {
        return new ArrayList<String>();
    }

    @Override
    public List<Extension> getExtensions() {
        return new ArrayList<Extension>();
    }

    @Override
    public Configurator getConfigurator() {
        return new Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                List<String> values = new ArrayList<String>();
                values.add("Basic " + new String(Base64.encodeBase64(credentials.getBytes())));
                headers.put("Authorization", values);
                logger.info("Headers: {}", headers);
                super.beforeRequest(headers);
            }
        };
    }

}
