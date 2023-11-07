package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.StartLargeFileUpload;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class StartLargeFileUploadDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        long size = in.readLong();
        int filenameSize = in.readInt();
        String filename = String.valueOf(in.readCharSequence(filenameSize, StandardCharsets.UTF_8));
        int directorySize = in.readInt();
        String directory = String.valueOf(in.readCharSequence(directorySize, StandardCharsets.UTF_8));
        var startLargeFileTransfer = StartLargeFileUpload.builder()
                .size(size)
                .filename(filename)
                .directory(directory)
                .build();
        out.add(startLargeFileTransfer);
    }
}
