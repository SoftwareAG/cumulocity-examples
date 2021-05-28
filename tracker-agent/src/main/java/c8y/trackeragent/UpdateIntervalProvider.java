package c8y.trackeragent;

import static com.cumulocity.rest.representation.tenant.OptionMediaType.OPTION;

import com.cumulocity.microservice.context.inject.TenantScope;
import com.cumulocity.microservice.context.inject.UserScope;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.configuration.TrackerConfiguration;

import javax.ws.rs.core.Response;

@Component
//@DeviceScope
@TenantScope
public class UpdateIntervalProvider {

	private final static Logger logger = LoggerFactory.getLogger(UpdateIntervalProvider.class);
	private final static String optionEndpoint = "tenant/system/options/device/update.interval";

	private final String path;
	private final RestConnector connector;

	@Autowired
	public UpdateIntervalProvider(TrackerConfiguration conf, PlatformParameters platformParameters) {
		this.connector = new RestConnector(platformParameters, new SilentResponseParser());
		String host = conf.getPlatformHost();
		if (host == null) {
			throw new RuntimeException("Host cannot be null for options repository.");
		}
		if (host.charAt(host.length() - 1) != '/') {
            host = host + "/";
        }

		this.path = host + optionEndpoint;
		logger.info("Will use the following path to get update interval option: {}", path);
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

	private static class SilentResponseParser extends ResponseParser {

		protected String getErrorRepresentation(Response response) {
			if (isJsonResponse(response)) {
				return super.getErrorRepresentation(response);
			}
			logger.error("Failed to parse error message to json.");
			return null;
		}

	}

}
