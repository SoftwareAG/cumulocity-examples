package c8y.trackeragent.protocol.rfv16.device;

public class RFV16Device {
    
    private String locationReportInterval;

    public String getLocationReportInterval() {
        return locationReportInterval;
    }

    public RFV16Device setLocationReportInterval(String locationReportInterval) {
        this.locationReportInterval = locationReportInterval;
        return this;
    }

    @Override
    public String toString() {
        return String.format("CobanDevice [locationReportInterval=%s]", locationReportInterval);
    }
}
