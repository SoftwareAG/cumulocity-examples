/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class MeasurementSubscriber extends Subscriber<MeasurementPubSub> {

    @Autowired
    private GatewayProperties gatewayProperties;

    @Autowired
    private MeasurementApi measurementApi;

    @Override
    public boolean isBatchingSupported() {
        return true;
    }

	@Override
	public int getBatchSize() {
		return gatewayProperties.getGatewayMaxBatchSize();
	}

    @Override
    public int getConcurrentSubscriptionsCount() {
        // 30% of the total threads available for gateway
        int count = gatewayProperties.getGatewayThreadPoolSize() * 30 / 100;

        return (count <= 0)? 3 : count;
    }

    @Override
    public void handleMessage(String message) {
        measurementApi.create(new MeasurementRepresentation(message));
    }

    @Override
    public void handleMessages(Collection<String> messageCollection) {
        measurementApi.createBulkWithoutResponse(new MeasurementCollectionRepresentation(messageCollection));
    }


    public static class MeasurementRepresentation extends com.cumulocity.rest.representation.measurement.MeasurementRepresentation {
        private String jsonString;

        public MeasurementRepresentation() {
        }

        MeasurementRepresentation(String jsonString) {
            this.jsonString = jsonString;
        }

        @Override
        public String toJSON() {
            return jsonString;
        }
    }

    public static class MeasurementCollectionRepresentation extends com.cumulocity.rest.representation.measurement.MeasurementCollectionRepresentation {

        private Collection<String> jsonStrings;

        public MeasurementCollectionRepresentation() {
        }

        MeasurementCollectionRepresentation(Collection<String> jsonStrings) {
            this.jsonStrings = jsonStrings;
        }

        @Override
        public String toJSON() {
            return "{\"measurements\":["
                    + String.join(",", jsonStrings)
                    + "]}";
        }
    }
}
