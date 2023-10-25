package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.FileUploadRequest;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class FileUploadRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        FileUploadRequest fileUploadRequest = (FileUploadRequest) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(fileUploadRequest.getFilename().length());
        out.writeCharSequence(fileUploadRequest.getFilename(), StandardCharsets.UTF_8);
        out.writeInt(fileUploadRequest.getSize());
//        out.writeInt(fileUploadRequest.getFilename().length());
//        out.writeLong(fileUploadRequest.getSize());
//        out.writeCharSequence(fileUploadRequest.getFilename(), StandardCharsets.UTF_8);
        out.writeBytes(fileUploadRequest.getData());
    }
}
