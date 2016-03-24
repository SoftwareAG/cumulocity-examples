package c8y.trackeragent;

import static com.cumulocity.rest.representation.tenant.OptionMediaType.OPTION;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;

public class UpdateIntervalProvider {
    
    private Logger logger = LoggerFactory.getLogger(UpdateIntervalProvider.class);

    private final static String optionEndpoint = "/tenant/system/options/device/update.interval";
    private String path;
    private RestConnector connector;

    public UpdateIntervalProvider(TrackerPlatform platform) {
        String host = platform.getHost();
        if (host == null) {
            throw new RuntimeException("Host cannot be null for options repository.");
        }
        
        this.path = host + optionEndpoint;
        logger.info("Will use the following path to get update interval option: {}", path);
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
            } else if (ex.getHttpStatus() == HttpStatus.SC_UNAUTHORIZED 
                    || ex.getHttpStatus() == HttpStatus.SC_FORBIDDEN) {
                logger.info("Access to tenant options forbidden. User does not have access...");
                return null;
            } else {
                throw ex;
            }
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

}
