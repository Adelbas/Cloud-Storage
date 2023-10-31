package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.StartLargeFileUpload;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class StartLargeFileUploadEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        var startLargeFileTransfer = (StartLargeFileUpload) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeLong(startLargeFileTransfer.getSize());
        out.writeInt(startLargeFileTransfer.getFilename().length());
        out.writeCharSequence(startLargeFileTransfer.getFilename(), StandardCharsets.UTF_8);
        out.writeInt(startLargeFileTransfer.getDirectory().length());
        out.writeCharSequence(startLargeFileTransfer.getDirectory(), StandardCharsets.UTF_8);
    }
}
