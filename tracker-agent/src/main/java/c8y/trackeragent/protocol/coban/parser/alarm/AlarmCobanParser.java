package c8y.trackeragent.protocol.coban.parser.alarm;

import static c8y.trackeragent.protocol.coban.parser.alarm.AlarmInfoParser.exact;
import static c8y.trackeragent.protocol.coban.parser.alarm.AlarmInfoParser.prefix;

import java.util.List;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.protocol.coban.parser.CobanParser;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
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
    protected boolean accept(String[] report) {
        if (report.length < 2) {
            return false;
        }
        return parseAlarmInfo(report) != null;
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        AlarmInfo alarmInfo = parseAlarmInfo(reportCtx.getReport());
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        AlarmRepresentation alarm = new AlarmRepresentation();
        //TODO
        device.createAlarm(alarm);
        return true;
    }

    @Override
    protected String doParse(String[] report) {
        return CobanServerMessages.extractImeiValue(report[0]);
    }
    
    private AlarmInfo parseAlarmInfo(String[] report) {
        String alarmType = report[1];
        AlarmInfo alarmInfo = null;
        for (AlarmInfoParser parser : alarmParsers) {
            alarmInfo = parser.parse(alarmType);
            if (alarmInfo != null) {
                break;
            }
        }
        return alarmInfo;
    }

}
