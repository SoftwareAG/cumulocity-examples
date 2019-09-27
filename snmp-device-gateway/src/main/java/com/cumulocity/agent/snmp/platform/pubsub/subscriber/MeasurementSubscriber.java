package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

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
    public void handleMessages(Collection<String> messageCollection) {
        measurementApi.createBulk(new MeasurementCollectionRepresentation(messageCollection));
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
