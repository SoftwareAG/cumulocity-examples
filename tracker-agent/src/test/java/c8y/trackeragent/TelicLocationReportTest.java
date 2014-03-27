package c8y.trackeragent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;

public class TelicLocationReportTest {
    
    public static final String IMEI = "187182";
    public static final Position POS = new Position();
    
    static {
        POS.setAlt(new BigDecimal("599"));
        POS.setLng(new BigDecimal("11.5864"));
        POS.setLat(new BigDecimal("48.0332"));
    }

    public static final String HEADER = "0000123456|262|02|003002016";
    public static final String REPORTSTR = "072118718299,200311121210,0,200311121210,115864,480332,3,4,67,4,,,599,11032,,010 1,00,238,0,0,0";
    public static final String[] REPORT = REPORTSTR.split(TelicConstants.FIELD_SEP);

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private TelicLocationReport telic = new TelicLocationReport(trackerAgent);

    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }

    private void verifyReport() {
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
        verify(device).setPosition(POS);
    }

    @Test
    public void report() {
        String imei = telic.parse(REPORT);
        telic.onParsed(REPORT, imei);
        assertEquals(IMEI, imei);
        verifyReport();
    }

    @Test
    public void run() throws IOException {
        Socket client = mock(Socket.class);

        byte[] bytes = getTelicReportBytes();

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ConnectedTelicTracker tracker = new ConnectedTelicTracker(client, bis, trackerAgent);
        tracker.run();
        verifyReport();
    }

    public static byte[] getTelicReportBytes() throws UnsupportedEncodingException {
        byte[] HEADERBYTES = HEADER.getBytes("US-ASCII");
        byte[] LOCATIONBYTES = TelicLocationReportTest.REPORTSTR.getBytes("US-ASCII");
        byte[] bytes = new byte[HEADERBYTES.length + 5 + LOCATIONBYTES.length + 1];

        int i = 0;
        for (byte b : HEADERBYTES) {
            bytes[i++] = b;
        }
        i += 5;
        for (byte b : LOCATIONBYTES) {
            bytes[i++] = b;
        }
        return bytes;
    }
}
