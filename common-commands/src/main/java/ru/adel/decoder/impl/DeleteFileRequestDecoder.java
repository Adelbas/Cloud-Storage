package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.DeleteFileRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class DeleteFileRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int pathLength = in.readInt();
        DeleteFileRequest deleteFileRequest = DeleteFileRequest.builder()
                .path(String.valueOf(in.readCharSequence(pathLength, StandardCharsets.UTF_8)))
                .build();
        out.add(deleteFileRequest);
    }
}
