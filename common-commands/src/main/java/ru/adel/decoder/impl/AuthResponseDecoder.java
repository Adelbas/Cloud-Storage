package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.AuthResponse;
import ru.adel.decoder.Decoder;

import java.util.List;

public class AuthResponseDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        AuthResponse authResponse = AuthResponse.builder()
                .isAuthenticated(in.readBoolean())
                .attemptsLeft(in.readInt())
                .build();
        out.add(authResponse);
    }
}
