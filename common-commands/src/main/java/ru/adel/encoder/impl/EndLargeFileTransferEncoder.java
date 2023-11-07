package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.EndLargeFileTransfer;
import ru.adel.encoder.Encoder;

public class EndLargeFileTransferEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        EndLargeFileTransfer endLargeFileTransfer = (EndLargeFileTransfer) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeBoolean(endLargeFileTransfer.isSucceed());
    }
}
