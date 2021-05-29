/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

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
