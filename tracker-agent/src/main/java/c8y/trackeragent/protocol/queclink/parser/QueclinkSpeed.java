package c8y.trackeragent.protocol.queclink.parser;

import org.springframework.stereotype.Component;

import c8y.trackeragent.context.ReportContext;

@Component
public class QueclinkSpeed {
    
    public static final String[] MOTION_REPORT = {
            "+RESP:GTSTT"
    };

    public Integer getSpeed(String[] report) { //throws InvalidValueException
        String reportType = report[0];
        return null;
    }
    
    public Integer getSpeed(ReportContext reportCtx) { //throws InvalidValueException
        String reportType = reportCtx.getReport()[0];
        return null;
    }
}
