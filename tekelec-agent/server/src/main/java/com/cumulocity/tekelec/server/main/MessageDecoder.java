package com.cumulocity.tekelec.server.main;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        out.add(in.readBytes(17 + payloadLength));
    }
}
