package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.FileDownloadRequest;
import ru.adel.encoder.Encoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileDownloadRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws IOException {
        FileDownloadRequest fileDownloadRequest = (FileDownloadRequest) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(fileDownloadRequest.getFilePath().length());
        out.writeCharSequence(fileDownloadRequest.getFilePath(), StandardCharsets.UTF_8);
    }
}
