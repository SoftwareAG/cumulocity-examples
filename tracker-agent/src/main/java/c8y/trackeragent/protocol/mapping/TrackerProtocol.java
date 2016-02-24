package c8y.trackeragent.protocol.mapping;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.protocol.coban.ConnectedCobanTracker;
import c8y.trackeragent.protocol.gl200.ConnectedGL200Tracker;
import c8y.trackeragent.protocol.rfv16.ConnectedRFV16Tracker;
import c8y.trackeragent.protocol.telic.ConnectedTelicTracker;

public enum TrackerProtocol {

    TELIC(ConnectedTelicTracker.class) {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte >= '0' && firstByte <= '9';
        }

    },
    GL200(ConnectedGL200Tracker.class) {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte < '0' || firstByte > '9';
        }

    },
    COBAN(ConnectedCobanTracker.class) {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '#';
        }

    },
    RFV16(ConnectedRFV16Tracker.class) {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '*';
        }

    };

    private final Class<? extends ConnectedTracker<?>> trackerClazz;

    private TrackerProtocol(Class<? extends ConnectedTracker<?>> trackerClazz) {
        this.trackerClazz = trackerClazz;
    }

    public Class<? extends ConnectedTracker<?>> getTrackerClass() {
        return trackerClazz;
    }

    public abstract boolean accept(byte firstByte);
}
