package c8y.trackeragent.protocol.telic;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.utils.message.TrackerMessage;

public class TelicMessage extends TrackerMessage {
    
    public static final String CONNECTION_HEADER = "0000123456|262|02|003002016";
    public static final String REPORT_HEADER = "     ";
    
    public TelicMessage() {
        super(TrackingProtocol.TELIC);
    }

    @Override
    public String asText() {
        StringBuilder result = new StringBuilder(CONNECTION_HEADER);
        for (Report report : reports) {
            result.append(REPORT_HEADER);
            result.append(report);
            result.append(reportSep);
        }
        return result.toString();
    }
    
    public static void main(String[] args) {
        System.out.println(CONNECTION_HEADER.length());
        System.out.println(REPORT_HEADER.length());
    }

}
