package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.FileMessage;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileMessageDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int size = in.readInt();
        byte[] data = new byte[size];
        in.readBytes(data, 0, size);
        int filenameSize = in.readInt();
        String filename = String.valueOf(in.readCharSequence(filenameSize, StandardCharsets.UTF_8));
        int directorySize = in.readInt();
        String directory = String.valueOf(in.readCharSequence(directorySize, StandardCharsets.UTF_8));
        FileMessage fileMessage = FileMessage.builder()
                .filename(filename)
                .directory(directory)
                .size(size)
                .data(data)
                .build();
        out.add(fileMessage);
    }
}
