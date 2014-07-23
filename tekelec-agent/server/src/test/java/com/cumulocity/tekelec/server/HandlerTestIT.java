package com.cumulocity.tekelec.server;


import java.io.DataOutputStream;
import java.net.Socket;

import org.junit.Test;

public class HandlerTestIT {

    byte[] bytes = { 0x02, 0x02, (byte) 0xD6, 0x08, (byte) 0x8E, 0x0A, (byte) 0xFC, 0x03, 0x54, 0x77, (byte) 0x80, 0x30, (byte) 0x87, 0x65, 0x05, 0x08, 0x0D,
            0x00, 0x33, (byte) 0x90, 0x00, 0x00, 0x00, (byte) 0x84, 0x02, 0x06, 0x09, 0x70, 0x28, 0x1B };

    @Test
    public void test() throws Exception {
        Socket socket = new Socket("integration.cumulocity.com", 8282);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        for (byte i : bytes) {
            out.writeByte(i);
            out.flush();
        }
    }
}
