package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.LogoutRequest;
import ru.adel.decoder.Decoder;

import java.util.List;

public class LogoutRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(LogoutRequest.builder().build());
    }
}
