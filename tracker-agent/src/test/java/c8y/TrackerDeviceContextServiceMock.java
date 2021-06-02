package c8y;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.service.TrackerDeviceContextService;

public class TrackerDeviceContextServiceMock extends TrackerDeviceContextService {

    public TrackerDeviceContextServiceMock() {
        super(null, null,null, null);
    }

    @Override
    public void executeWithContext(String tenant, Runnable runnable) {
        runnable.run();
    }

    @Override
    public void executeWithContext(String tenant, String imei, TrackingProtocol trackingProtocol, Runnable runnable) {
        runnable.run();
    }
}
