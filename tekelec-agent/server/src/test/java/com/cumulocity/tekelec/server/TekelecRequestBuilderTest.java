package com.cumulocity.tekelec.server;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.cumulocity.tekelec.PayloadData;
import com.cumulocity.tekelec.TekelecRequest;
import com.cumulocity.tekelec.TekelecRequestBuilder;

public class TekelecRequestBuilderTest {

    byte[] requestBytes = { 0x02, 0x02, 0x15, 0x08, (byte) 0x86, 0x11, (byte) 0xFB, 0x00, 0x11, 0x41, 0x20, 0x01, 0x78, 0x51, 0x16, 0x08, 0x7B, 0x00,
            0x33, (byte) 0x90, 0x00, 0x00, 0x00, (byte) 0x84, 0x02, 0x06, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x09,
            0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1C, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70,
            0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x2E };

    @Test
    public void shouldCorrectlyParseRequest() throws Exception {
        TekelecRequest tekelecRequest = new TekelecRequestBuilder().build(requestBytes);
        
        assertThat(tekelecRequest.getProductType()).isEqualTo(2);
        assertThat(tekelecRequest.getHardwareRevision()).isEqualTo(2);
        assertThat(tekelecRequest.getFirmwareRevision()).isEqualTo(21);
        assertThat(tekelecRequest.getContactReason()).isEqualTo(asList("Manual"));
        assertThat(tekelecRequest.getAlarmAndStatus()).isEqualTo(asList("GO Active", "Limits 3 Status",
                "Limits 2 Status"));
        assertThat(tekelecRequest.getGsmRssi()).isEqualTo(17);
        assertThat(tekelecRequest.getBattery()).isEqualTo(BigDecimal.valueOf(5.7));
        assertThat(tekelecRequest.getImei()).isEqualTo("0011412001785116");
        assertThat(tekelecRequest.getMessageType()).isEqualTo((byte)8);
        assertPayloadData(tekelecRequest);
    }

    private void assertPayloadData(TekelecRequest tekelecRequest) {
        for (int i = 0; i < 4; i++) {
            assertThat(tekelecRequest.getPayloadData().get(i)).isEqualTo(new PayloadData(9, 26, 10, 27));
        }
        assertThat(tekelecRequest.getPayloadData().get(4)).isEqualTo(new PayloadData(9, 26, 10, 28));
        for (int i = 5; i < 10; i++) {
            assertThat(tekelecRequest.getPayloadData().get(i)).isEqualTo(new PayloadData(9, 26, 10, 27));
        }
        for (int i = 10; i < 28; i++) {
            assertThat(tekelecRequest.getPayloadData().get(i)).isEqualTo(new PayloadData(0, -30, 0, 0));
        }
    }

    //    @Test
    //    public void ittest() throws Exception {
    //        Socket socket = new Socket("integration.cumulocity.com", 8282);
    //        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    //        for (byte i : bytes) {
    //            out.writeByte(i);
    //            out.flush();
    //        }
    //    }

}
