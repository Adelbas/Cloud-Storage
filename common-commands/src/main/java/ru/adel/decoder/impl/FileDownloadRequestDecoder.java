package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.FileDownloadRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileDownloadRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int filePathSize = in.readInt();
        String filePath = String.valueOf(in.readCharSequence(filePathSize, StandardCharsets.UTF_8));

        out.add(FileDownloadRequest.builder().filePath(filePath).build());
    }
}
