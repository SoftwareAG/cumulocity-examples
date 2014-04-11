package c8y.trackeragent.exception;

import com.cumulocity.sdk.client.SDKException;

public class SDKExceptions {

    public static SDKException narrow(Exception e, String sdkMessage) {
        Throwable cause = e.getCause();
        if (e instanceof SDKException) {
            throw (SDKException) e;
        } else if (cause != null && cause instanceof SDKException) {
            throw (SDKException) cause;
        } else {
            throw new SDKException(sdkMessage, e);
        }

    }

}
