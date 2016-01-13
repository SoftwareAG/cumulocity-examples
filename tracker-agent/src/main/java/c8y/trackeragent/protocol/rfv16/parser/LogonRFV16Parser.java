package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;

import com.cumulocity.sdk.client.SDKException;

public class LogonRFV16Parser extends RFV16Parser implements Parser {
    
    private static Logger logger = LoggerFactory.getLogger(LogonRFV16Parser.class);
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        return false;
    }

}
