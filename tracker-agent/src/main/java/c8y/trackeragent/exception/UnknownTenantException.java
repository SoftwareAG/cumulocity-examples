package c8y.trackeragent.exception;

import com.cumulocity.sdk.client.SDKException;

public class UnknownTenantException extends SDKException {

    private static final long serialVersionUID = 7464141119234912762L;

    public static UnknownTenantException forTenantId(String tenantId) {
        return new UnknownTenantException("Unknwon tenant: " + tenantId);
    }

    private UnknownTenantException(String message) {
        super(message);
    }

}
