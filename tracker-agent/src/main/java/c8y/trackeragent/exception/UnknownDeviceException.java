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

public class UnknownDeviceException extends SDKException {

    private static final long serialVersionUID = 7464141119234912762L;

    public static UnknownDeviceException forImei(String imei) {
        return new UnknownDeviceException("Unknwon device for imei " + imei);
    }

    private UnknownDeviceException(String message) {
        super(message);
    }

}
