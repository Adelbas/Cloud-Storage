package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.CreateFolderRequest;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class CreateFolderRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        CreateFolderRequest createFolderRequest = (CreateFolderRequest) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(createFolderRequest.getFolderName().length());
        out.writeInt(createFolderRequest.getFolderPath().length());
        out.writeCharSequence(createFolderRequest.getFolderName(), StandardCharsets.UTF_8);
        out.writeCharSequence(createFolderRequest.getFolderPath(), StandardCharsets.UTF_8);
    }
}
