package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.FileMessage;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class FileMessageEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        FileMessage fileMessage = (FileMessage) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(fileMessage.getSize());
        out.writeBytes(fileMessage.getData());
        out.writeInt(fileMessage.getFilename().length());
        out.writeCharSequence(fileMessage.getFilename(), StandardCharsets.UTF_8);
        out.writeInt(fileMessage.getDirectory().length());
        out.writeCharSequence(fileMessage.getDirectory(), StandardCharsets.UTF_8);
    }
}
