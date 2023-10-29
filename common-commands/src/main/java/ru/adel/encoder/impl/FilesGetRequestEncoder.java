package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.FilesGetRequest;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class FilesGetRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        FilesGetRequest filesGetRequest = (FilesGetRequest) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(filesGetRequest.getDirectory().length());
        out.writeCharSequence(filesGetRequest.getDirectory(), StandardCharsets.UTF_8);
    }
}
