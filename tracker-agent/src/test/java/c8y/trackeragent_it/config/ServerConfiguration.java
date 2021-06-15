/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.config;

import c8y.trackeragent.Main;
import com.cumulocity.sms.client.SmsMessagingApi;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

@Configuration
@Import(Main.class)
@PropertySource(value = { "classpath:tracker-agent-server.properties" })
public class ServerConfiguration {

    @Bean
    public SmsMessagingApi outgoingMessagingClient() {
        return Mockito.mock(SmsMessagingApi.class);
    }
}
