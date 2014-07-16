package com.cumulocity.tixi.server.main;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private String[] contactReasons = { "DynLim2 Status", "DymLin1 Status", "TSP Requested", "Re-boot", "Manual", "Server Requested",
            "Alarm", "Scheduled" };

    private String[] alarmAndStatuses = { "GO Active", "BOR Reset", "WDT Reset", "Limp Along RTC", "Bund Status Closed", "Limits 3 Status",
            "Limits 2 Status", "Limits 1 Status" };

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            if (in.readableBytes() >= 15) {
                int productType = readInt(in);
                int hardwareRevision = readInt(in);
                int firmwareRevision = readInt(in);
                byte contactReasonByte = in.readByte();
                List<String> contactReason = getMatchingResult(contactReasonByte, contactReasons);
                byte alarmAndStatusByte = in.readByte();
                List<String> alarmAndStatus = getMatchingResult(alarmAndStatusByte, alarmAndStatuses);
                byte gsmRssi = in.readByte();
                float battery = ((float) extractRightBits(in.readByte(), 5) + 30) / 10;
                String imei = "";
                for (int i = 0 ; i < 8 ; i++) {
                    imei += String.format("%02X", readInt(in));
                }
                in.skipBytes(11);
                int auxRssi = readInt(in);
                int tempInCelsius = (readInt(in) >> 1) - 30;
                int sonitResultCode;
//                System.out.println(productType);
//                System.out.println(hardwareRevision);
//                System.out.println(firmwareRevision);
//                System.out.println(contactReason);
//                System.out.println(alarmAndStatus);
//                System.out.println(gsmRssi);
//                System.out.println(battery);
//                System.out.println(imei);
//                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private int readInt(ByteBuf in) {
        return (int) in.readByte() & 0xff;
    }
    
    private int extractRightBits(byte x, int numBits) {
        if (numBits < 1) {
            return 0;
        }
        if (numBits > 32) {
            return x;
        }
        int mask = (1 << numBits) - 1;
        return x & mask;
    }

    private List<String> getMatchingResult(byte crByte, String[] values) {
        String bin = String.format("%8s", Integer.toBinaryString(crByte & 0xFF)).replace(' ', '0');
        List<String> result = new ArrayList<String>();
        char arr[] = bin.toCharArray();
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == '1') {
                result.add(values[i]);
            }
        }
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
