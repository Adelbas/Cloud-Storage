package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.encoder.Encoder;

public class UnknownCmdEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        out.writeInt(msg.getCommandType().ordinal());
    }
}
