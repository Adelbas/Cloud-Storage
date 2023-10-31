package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.StartLargeFileDownload;
import ru.adel.encoder.Encoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StartLargeFileDownloadEncoder implements Encoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws IOException {
        StartLargeFileDownload startLargeFileDownload = (StartLargeFileDownload) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(startLargeFileDownload.getFilePath().length());
        out.writeCharSequence(startLargeFileDownload.getFilePath(), StandardCharsets.UTF_8);
    }
}
