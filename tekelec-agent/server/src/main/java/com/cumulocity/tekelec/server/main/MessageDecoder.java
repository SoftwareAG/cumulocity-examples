package com.cumulocity.tekelec.server.main;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tekelec.TekelecRequest;
import com.cumulocity.tekelec.TekelecRequestBuilder;

public class MessageDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        logger.debug("Got some message");
        if (in.readableBytes() < 17) {
            return;
        }
        logger.debug("Message longer than 17 bytes");
        byte payloadLength = in.getByte(16);
        logger.debug("Payload length: " + payloadLength);
        if (in.readableBytes() < 17 + payloadLength) {
            return;
        }
        printRequest(in);
        
        byte[] dst = new byte[17 + payloadLength];
        in.readBytes(dst);
        TekelecRequest tekelecRequest = new TekelecRequestBuilder().build(dst);
        logger.debug("Parsed request: " + tekelecRequest);
        
        out.add(tekelecRequest);
    }
    
    private void printRequest(ByteBuf in) {
        ByteBuf copy = in.copy();
        while(copy.isReadable()) {
            logger.debug("" + readInt(copy));
        }
    }
    
    private int readInt(ByteBuf in) {
        return (int) in.readByte() & 0xff;
    }
}
