package c8y.trackeragent.utils;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Optional;

import static c8y.trackeragent.utils.SDKExceptionHandler.HandleExceptionResult.*;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class SDKExceptionHandler {

    public enum  HandleExceptionResult {
        OTHER_STATUS,
        OTHER_EXCEPTION,
        STATUS_MATCHES,
    }

    /**
     * Checks if is instance of SDKException or one of ancestors is instance of SDKExceptions and has particular status.
     *
     * @return
     *  NON_SDK_EXCEPTION if exception or one of ancestors is not SDKException
     *  STATUS_MATCHES if exception has not accepted status
     *  OTHER_STATUS otherwise
     */
    public static HandleExceptionResult handleSDKException(Exception e, int... incorrectStatuses) {
        final Optional<SDKException> sdkException = findException(SDKException.class, e);
        if (!sdkException.isPresent()) {
            return OTHER_EXCEPTION;
        }

        if (contains(sdkException.get().getHttpStatus(), incorrectStatuses)) {
            return STATUS_MATCHES;
        }

        return OTHER_STATUS;
    }

    public static boolean contains(int e, int... ints) {
        for (final int status : ints) {
            if (e == status) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Throwable> Optional<T> findException(Class<T> clazz, Throwable ex) {
        if (ex == null) {
            return absent();
        }
        if (clazz.isInstance(ex)) {
            return of((T) ex);
        }
        return findException(clazz, ex.getCause());
    }
}
