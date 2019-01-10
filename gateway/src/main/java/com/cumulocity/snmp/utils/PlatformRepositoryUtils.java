package com.cumulocity.snmp.utils;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.FatalBeanException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Throwables.propagate;

@Slf4j
@UtilityClass
public class PlatformRepositoryUtils {

    public static <T> Optional<T> handleSuccess(@Nonnull Optional<T> reference) {
        return reference;
    }

    public static <T> Optional<T> handleSuccess(@Nullable T nullableReference) {
        return fromNullable(nullableReference);
    }

    public static <T> Optional<T> handleException(Throwable ex) {
        printError(ex);
        if (ex instanceof FatalBeanException) {
            return handleException(ex.getCause());
        } else if (notFoundResponse(ex)) {
            return Optional.absent();
        } else if (unauthorizedResponse(ex)) {
            return Optional.absent();
        } else {
            throw propagate(ex);
        }
    }

    private static boolean notFoundResponse(Throwable ex) {
        return ex instanceof SDKException && ((SDKException) ex).getHttpStatus() == 404;
    }

    private static boolean unauthorizedResponse(Throwable ex) {
        return ex instanceof SDKException && ((SDKException) ex).getHttpStatus() == 401;
    }

    private static void printError(Throwable ex) {
        final String message = ex.getMessage();
        final String lineSeparator = "[\\r\\n]";
        log.error(message.split(lineSeparator)[0]);
    }
}
