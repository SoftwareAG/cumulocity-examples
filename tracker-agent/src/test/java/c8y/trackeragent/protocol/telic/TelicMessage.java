package c8y.trackeragent.protocol.telic;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.utils.message.TrackerMessage;

public class TelicMessage extends TrackerMessage {
    
    public static final String HEADER = "0000123456|262|02|003002016";
    
    public TelicMessage() {
        super(TrackingProtocol.TELIC);
    }

    @Override
    public byte[] asBytes() {
        Iterable<String> reportStrs = Iterables.transform(getReports(), Functions.toStringFunction());
        return asBytes(reportStrs);
    }

    public static byte[] asBytes(Iterable<String> reports) {
        int reportsSize = sizeOf(reports);
        byte[] HEADERBYTES = asBytes(HEADER);
        byte[] bytes = new byte[HEADERBYTES.length + reportsSize + 1];
        
        int i = 0;
        for (byte b : HEADERBYTES) {
            bytes[i++] = b;
        }
        for(String report : reports) {
            i += 5;
            byte[] reportBytes = asBytes(report);
            for (byte b : reportBytes) {
                bytes[i++] = b;
            }
        }
        return bytes;
    }
    
    private static int sizeOf(Iterable<String> reports) {
        int result = 0;
        for (String report : reports) {
            result += asBytes(report).length;
            result += 5;
        }
        return result;
    }

}
