package c8y.trackeragent.protocol.coban.parser.alarm;

import static c8y.trackeragent.protocol.coban.parser.alarm.AlarmInfoParser.exact;
import static c8y.trackeragent.protocol.coban.parser.alarm.AlarmInfoParser.prefix;

import java.util.List;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.parser.CobanParser;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.collect.ImmutableList;

public class AlarmCobanParser extends CobanParser {
    
    // @formatter:off
    public final static List<AlarmInfoParser> alarmParsers = ImmutableList.<AlarmInfoParser>builder()
            .add(exact("help me")) //sos
            .add(exact("low battery"))
            .add(exact("move"))
            .add(exact("speed"))
            .add(exact("stockade"))
            .add(exact("ac alarm")) //power off
            .add(exact("door alarm"))
            .add(exact("sensor alarm")) //shock
            .add(exact("acc alarm")) 
            .add(exact("accident alarm")) 
            .add(exact("bonnet alarm")) 
            .add(exact("footbrake alarm")) 
            .add(prefix("T", ":")) //temperature 
            .add(prefix("oil", " ")) //fuel
            .build(); 
    // @formatter:on
    
    public AlarmCobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        // TODO
        return false;
    }

    @Override
    protected boolean accept(String[] report) {
        if(report.length < 2) {
            return false;
        }
        return parseAlarmInfo(report[1]) != null;
    }

    @Override
    protected String doParse(String[] report) {
        return extractImeiValue(report[0]);
    }
    
    private AlarmInfo parseAlarmInfo(String reportType) {
        AlarmInfo alarmInfo = null;
        for (AlarmInfoParser parser : alarmParsers) {
            alarmInfo = parser.parse(reportType);
            if (alarmInfo != null) {
                break;
            }
        }
        return alarmInfo;
    }

}
