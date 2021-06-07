package c8y.trackeragent_it.simulator;

import static c8y.trackeragent.utils.Positions.aPosition;

import java.math.BigDecimal;

import c8y.Position;

public class PositionIterator {
    
    private Double lat;
    private final Double lng;
    private final Double latStep;
    
    public PositionIterator(double lat, double lng, double latStep) {
        this.lat = lat;
        this.lng = lng;
        this.latStep = latStep;
    }
    
    public Position next() {
        lat += latStep;
        return current();
    }
    
    public Position current() {
        return aPosition(BigDecimal.valueOf(lat), BigDecimal.valueOf(lng), BigDecimal.valueOf(0));
    }
}
