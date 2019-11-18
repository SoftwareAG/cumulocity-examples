package c8y.trackeragent.protocol.telic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.utils.ByteHelper;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;

public class TelicDeviceMessagesTest {
    
    TelicDeviceMessages deviceMessages = new TelicDeviceMessages();
    
    @Test
    public void bytesFormShouldBeTheSameWhatStringForm() throws Exception {
        String imei = "abcd";
        TrackerMessage msg = deviceMessages.positionUpdate(imei, Positions.ZERO);
        
        assertThat(msg.asText()).isEqualTo(ByteHelper.getString(msg.asBytes()));

    }

}
