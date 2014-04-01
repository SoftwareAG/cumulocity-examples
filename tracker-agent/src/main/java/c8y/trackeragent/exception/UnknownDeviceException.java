package c8y.trackeragent.exception;

import com.cumulocity.sdk.client.SDKException;

public class UnknownDeviceException extends SDKException {

    private static final long serialVersionUID = 7464141119234912762L;

    public static UnknownDeviceException forImei(String imei) {
        return new UnknownDeviceException("Unknwon device for imei " + imei);
    }

    private UnknownDeviceException(String message) {
        super(message);
    }

}
