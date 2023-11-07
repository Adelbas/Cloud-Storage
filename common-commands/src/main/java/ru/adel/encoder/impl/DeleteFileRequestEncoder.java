package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.DeleteFileRequest;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class DeleteFileRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        DeleteFileRequest deleteFileRequest = (DeleteFileRequest) msg;
        out.writeInt(deleteFileRequest.getCommandType().ordinal());
        out.writeInt(deleteFileRequest.getPath().length());
        out.writeCharSequence(deleteFileRequest.getPath(), StandardCharsets.UTF_8);
    }
}
