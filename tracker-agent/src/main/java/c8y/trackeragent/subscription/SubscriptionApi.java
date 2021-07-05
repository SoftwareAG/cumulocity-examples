package c8y.trackeragent.subscription;

import com.cumulocity.rest.representation.application.ApplicationReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.application.ApplicationReferenceRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
class SubscriptionApi {

    private final static String URL = "/tenant/tenants/{tenant}/applications?pageSize=2000";
    private final static String TENANT = "{tenant}";

    @Autowired
    private PlatformImpl platform;

    ApplicationReferenceCollectionRepresentation getApplications(String tenant) {
        return platform.rest().get(
                URL.replace(TENANT, tenant),
                MediaType.APPLICATION_JSON_TYPE,
                ApplicationReferenceCollectionRepresentation.class
        );
    }

    void subscribe(String tenant, ApplicationReferenceRepresentation application) {
        platform.rest().post(
                URL.replace(TENANT, tenant),
                MediaType.APPLICATION_JSON_TYPE,
                application
        );
    }

    String getApplicationKey() {
        return platform.getApplicationKey();
    }
}
