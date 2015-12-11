package c8y.trackeragent.protocol.coban.device;

public class CobanDevice {
    
    private String locationReportInterval;

    public String getLocationReportInterval() {
        return locationReportInterval;
    }

    public CobanDevice setLocationReportInterval(String locationReportInterval) {
        this.locationReportInterval = locationReportInterval;
        return this;
    }

    @Override
    public String toString() {
        return String.format("CobanDevice [locationReportInterval=%s]", locationReportInterval);
    }
}
