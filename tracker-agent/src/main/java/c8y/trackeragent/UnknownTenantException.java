package c8y.trackeragent;

import com.cumulocity.sdk.client.SDKException;

public class UnknownTenantException extends SDKException {

    private static final long serialVersionUID = 7464141119234912762L;

    public UnknownTenantException(String imei) {
        super("Unknwon tenant for imei " + imei);
    }
    
}
