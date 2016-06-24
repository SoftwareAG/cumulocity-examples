package c8y.trackeragent.protocol.rfv16.message;

import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.utils.message.TrackerMessage;

public class RFV16ServerMessagesTest {
    
    private static final String HHMMSS = "010000";
    
    private final RFV16ServerMessages messages = new RFV16ServerMessages();
    
    @Before
    public void init() {
        DateTime dateTime = RFV16ServerMessages.HHMMSS.parseDateTime(HHMMSS);
        DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());
    }
    
    @Test
    public void createPositioningMonitoringCommand() throws Exception {
        TrackerMessage actual = messages.reportMonitoringCommand("7893267561", "5");
        
        assertThat(actual.asText()).isEqualTo("*HQ,7893267561,D1," + HHMMSS + ",5,1#");
    }

}
