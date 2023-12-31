package ru.adel.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;

import java.io.IOException;

/**
 * Interface that provides encoding for Command object
 */
public interface Encoder {
    void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception;
}
