package c8y.trackeragent;

import com.cumulocity.sdk.client.SDKException;

public abstract class GL200Parser implements Parser {

    @Override
    public String parse(String[] report) throws SDKException {
        return report.length > 2 ? report[2] : null;
    }

}
