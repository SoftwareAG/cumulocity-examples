package c8y.trackeragent.protocol.queclink.parser;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.tracker.Parser;

public abstract class QueclinkParser implements Parser, QueclinkFragment {
    
    protected static final String PASSWORD = "gl200";
    protected static QueclinkDevice queclinkDevice = new QueclinkDevice();

    @Override
    public String parse(String[] report) throws SDKException {
        return report.length > 2 ? report[2] : null;
    }

}
