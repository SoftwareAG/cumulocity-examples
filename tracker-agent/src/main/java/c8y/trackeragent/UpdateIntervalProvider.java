package c8y.trackeragent;

import static com.cumulocity.rest.representation.tenant.OptionMediaType.OPTION;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpStatus;

import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;

public class UpdateIntervalProvider {

    private final static String optionEndpoint = "/system/options/device/update.interval";
    private String path;
    private RestConnector connector;

    public UpdateIntervalProvider(TrackerPlatform platform) {
        String host = platform.getHost();
        if (host == null) {
            throw new RuntimeException("Host cannot be null for options repository.");
        }
        
        try {
            this.path = URLEncoder.encode(host + optionEndpoint, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode options url.");
        }
        connector = new RestConnector(platform.getPlatformParameters(), new ResponseParser());
    }
    
    public Integer findUpdateInterval() {
        try {
            OptionRepresentation option = connector.get(path, OPTION, OptionRepresentation.class);
            return Integer.parseInt(option.getValue());
        } catch (SDKException ex) {
            if (ex.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
                return null;
            } else {
                throw ex;
            }
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

}
