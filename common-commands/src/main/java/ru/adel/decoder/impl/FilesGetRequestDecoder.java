package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.FilesGetRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FilesGetRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int directorySize = in.readInt();
        String directory = String.valueOf(in.readCharSequence(directorySize, StandardCharsets.UTF_8));

        out.add(FilesGetRequest.builder().directory(directory).build());
    }
}
