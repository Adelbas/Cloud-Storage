package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.AuthResponse;
import ru.adel.encoder.Encoder;

public class AuthResponseEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        AuthResponse authResponse = (AuthResponse) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeBoolean(authResponse.isAuthenticated());
        out.writeInt(authResponse.getAttemptsLeft());
    }
}
