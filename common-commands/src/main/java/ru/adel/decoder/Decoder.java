package ru.adel.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Interface that provides decoding for Command object
 */
public interface Decoder {
    void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out);
}
