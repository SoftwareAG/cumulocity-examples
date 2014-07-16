package com.cumulocity.tixi.server;

import java.io.DataOutputStream;
import java.net.Socket;

import org.junit.Test;

public class HandlerTest {

    byte[] bytes = { 0x02, 0x02, 0x15, 0x08, (byte) 0x86, 0x11, (byte) 0xFB, 0x00, 0x11, 0x41, 0x20, 0x01, 0x78, 0x51, 0x16, 0x08, 0x7B,
            0x00, 0x33, (byte) 0x90, 0x00, 0x00, 0x00, (byte) 0x84, 0x02, 0x06, 0x09, 0x70, 0x28, 0x1B };

    //            0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28,
    //            0x1B, 0x09, 0x70, 0x28, 0x1C, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B, 0x09, 0x70, 0x28, 0x1B };

    @Test
    public void test() throws Exception {
        Socket socket = new Socket("localhost", 8088);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        for (byte i : bytes) {
            out.writeByte(i);
            out.flush();
        }
    }
    

    //        byte b1 = (byte) 251;
    //        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
    //        System.out.println(s1); // 10000001
    //
    //        int anUnsignedByte = (int) b1 & 0xff;
    //        System.out.println(anUnsignedByte);
    //    }

}
