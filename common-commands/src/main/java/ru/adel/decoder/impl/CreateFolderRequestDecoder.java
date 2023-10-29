package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.CreateFolderRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CreateFolderRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int folderNameLength = in.readInt();
        int folderPathLength = in.readInt();
        CreateFolderRequest createFolderRequest = CreateFolderRequest.builder()
                .folderName(String.valueOf(in.readCharSequence(folderNameLength, StandardCharsets.UTF_8)))
                .folderPath(String.valueOf(in.readCharSequence(folderPathLength, StandardCharsets.UTF_8)))
                .build();
        out.add(createFolderRequest);
    }
}
