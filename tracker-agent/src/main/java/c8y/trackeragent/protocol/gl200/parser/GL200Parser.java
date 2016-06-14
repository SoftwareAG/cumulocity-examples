package c8y.trackeragent.protocol.gl200.parser;

import c8y.trackeragent.Parser;

import com.cumulocity.sdk.client.SDKException;

public abstract class GL200Parser implements Parser, GL200Fragment {
    
    protected static final String PASSWORD = "gl200";

    @Override
    public String parse(String[] report) throws SDKException {
        return report.length > 2 ? report[2] : null;
    }

}
