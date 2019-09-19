package com.cumulocity.agent.snmp.pubsub.subscriber;

import com.cumulocity.agent.snmp.pubsub.service.MeasurementPubSub;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class MeasurementSubscriber extends Subscriber<MeasurementPubSub> {

    @Autowired
    private MeasurementApi measurementApi;

    @Override
    public boolean isBatchingSupported() {
        return true;
    }

    @Override
    public int getBatchSize() {
        // 200 is the default value, which should suffice. This can be made configurable if required.
        return 200;
    }

    @Override
    public void handleMessage(String message) {
        measurementApi.create(new MeasurementRepresentation(message));
    }

    @Override
    public void handleBulkMessages(Collection<String> messageCollection) {
        measurementApi.createBulkWithoutResponse(new MeasurementCollectionRepresentation(messageCollection));
    }


    public static class MeasurementRepresentation extends com.cumulocity.rest.representation.measurement.MeasurementRepresentation {
        private String jsonString;

        public MeasurementRepresentation() {
        }

        public MeasurementRepresentation(String jsonString) {
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

        public MeasurementCollectionRepresentation(Collection<String> jsonStrings) {
            this.jsonStrings = jsonStrings;
        }

        @Override
        public String toJSON() {
            return "{\"measurements\":["
                    + jsonStrings.stream().collect(Collectors.joining(","))
                    + "]}";
        }
    }
}
