package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.EndLargeFileTransfer;
import ru.adel.decoder.Decoder;

import java.util.List;

public class EndLargeFileTransferDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(EndLargeFileTransfer.builder().isSucceed(in.readBoolean()).build());
    }
}
