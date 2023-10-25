package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.AuthRequest;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class AuthRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        AuthRequest authRequest = (AuthRequest) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(authRequest.getUsername().length());
        out.writeInt(authRequest.getPassword().length());
        out.writeCharSequence(authRequest.getUsername(), StandardCharsets.UTF_8);
        out.writeCharSequence(authRequest.getPassword(), StandardCharsets.UTF_8);
    }
}
