/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.sms;

import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OptionsAuthorizationSupplier extends SmsMessagingApiImpl.SmsCredentialsProvider {

    private final ThreadLocal<DeviceCredentials> credentials = new ThreadLocal<>();

    @Autowired
    private TrackerConfiguration trackerConfiguration;

    public void set(DeviceCredentials tenant) {
        credentials.set(tenant);
    }

    public void clear() {
        credentials.remove();
    }

    @Override
    public String getTenant() {
        return credentials.get().getTenant();
    }

    @Override
    public String getUsername() {
        return trackerConfiguration.getSmsGatewayUser();
    }

    @Override
    public String getPassword() {
        return trackerConfiguration.getSmsGatewayPassword();
    }
}
