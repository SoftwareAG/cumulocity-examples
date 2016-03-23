package c8y.trackeragent;

import static com.cumulocity.rest.representation.tenant.OptionMediaType.OPTION;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;

public class UpdateIntervalProvider {
    
    private Logger logger = LoggerFactory.getLogger(UpdateIntervalProvider.class);

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
            logger.info("Will use the following path to get update interval option: {}", path);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode options url.");
        }
        connector = new RestConnector(platform.getPlatformParameters(), new ResponseParser());
    }
    
    public Integer findUpdateInterval() {
        logger.info("Find update interval in tenant options.");
        try {
            OptionRepresentation option = connector.get(path, OPTION, OptionRepresentation.class);
            logger.info("Update interval value is: {}", option.getValue());
            return Integer.parseInt(option.getValue());
        } catch (SDKException ex) {
            if (ex.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
                logger.info("Interval option not found.");
                return null;
            } else {
                throw ex;
            }
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

}
