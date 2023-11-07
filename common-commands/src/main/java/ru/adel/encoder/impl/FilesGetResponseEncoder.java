package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.FilesGetResponse;
import ru.adel.encoder.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FilesGetResponseEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws IOException {
        FilesGetResponse filesGetResponse = (FilesGetResponse) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(filesGetResponse.getFiles().size());

        for (File file : filesGetResponse.getFiles()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(file);
            out.writeInt(byteArrayOutputStream.size());
            out.writeBytes(byteArrayOutputStream.toByteArray());
            objectOutputStream.close();
            byteArrayOutputStream.close();
        }
    }
}