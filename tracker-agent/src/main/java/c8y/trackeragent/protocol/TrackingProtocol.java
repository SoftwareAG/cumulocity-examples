package c8y.trackeragent.protocol;

import c8y.trackeragent.protocol.aplicomd.ConnectedAplicomDTracker;
import c8y.trackeragent.protocol.coban.ConnectedCobanTracker;
import c8y.trackeragent.protocol.mt90g.ConnectedMT90GTracker;
import c8y.trackeragent.protocol.queclink.ConnectedQueclinkTracker;
import c8y.trackeragent.protocol.rfv16.ConnectedRFV16Tracker;
import c8y.trackeragent.protocol.telic.ConnectedTelicTracker;
import c8y.trackeragent.tracker.BaseConnectedTracker;

public enum TrackingProtocol {

    TELIC(ConnectedTelicTracker.class, "\0", ",") {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte >= '0' && firstByte <= '9';
        }

    },
    QUECLINK(ConnectedQueclinkTracker.class, "$", ",") {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte < '0' || firstByte > '9';
        }

    },
    COBAN(ConnectedCobanTracker.class, ";", ",") {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '#';
        }

    },
    RFV16(ConnectedRFV16Tracker.class, "#", ",") {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '*';
        }

    }, 
    MT90G(ConnectedMT90GTracker.class, "\n", ",") {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '$';
        }
    },
    APLICOM_D(ConnectedAplicomDTracker.class, "separatorsNotUsed", "") {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == 'D';
        }
    };

    private final Class<? extends BaseConnectedTracker<?>> trackerClazz;
    private final String reportSeparator;
    private final String fieldSeparator;

    private TrackingProtocol(Class<? extends BaseConnectedTracker<?>> trackerClazz, String reportSeparator, String fieldSeparator) {
        this.trackerClazz = trackerClazz;
        this.reportSeparator = reportSeparator;
        this.fieldSeparator = fieldSeparator;
    }

    public Class<? extends BaseConnectedTracker<?>> getTrackerClass() {
        return trackerClazz;
    }

    public abstract boolean accept(byte firstByte);

    public String getReportSeparator() {
        return reportSeparator;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
    }
}
