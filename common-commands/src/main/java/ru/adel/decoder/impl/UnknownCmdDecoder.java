package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.UnknownCmdResponse;
import ru.adel.decoder.Decoder;

import java.util.List;

public class UnknownCmdDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(UnknownCmdResponse.builder().build());
    }
}
