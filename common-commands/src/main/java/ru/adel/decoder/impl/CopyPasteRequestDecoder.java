package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.CopyPasteRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CopyPasteRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int copyFromPathLength = in.readInt();
        int pasteToPathLength = in.readInt();
        int filenameLength = in.readInt();
        CopyPasteRequest copyPasteRequest = CopyPasteRequest.builder()
                .copyFromPath(String.valueOf(in.readCharSequence(copyFromPathLength, StandardCharsets.UTF_8)))
                .pasteToPath(String.valueOf(in.readCharSequence(pasteToPathLength,StandardCharsets.UTF_8)))
                .filename(String.valueOf(in.readCharSequence(filenameLength,StandardCharsets.UTF_8)))
                .build();
        out.add(copyPasteRequest);
    }
}
