package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.AuthRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AuthRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int usernameLength = in.readInt();
        int passwordLength = in.readInt();
        AuthRequest authRequest = AuthRequest.builder()
                .username(String.valueOf(in.readCharSequence(usernameLength, StandardCharsets.UTF_8)))
                .password(String.valueOf(in.readCharSequence(passwordLength, StandardCharsets.UTF_8)))
                .build();
        out.add(authRequest);
    }
}
