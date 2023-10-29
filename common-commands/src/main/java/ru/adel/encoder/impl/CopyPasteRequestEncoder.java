package ru.adel.encoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;
import ru.adel.command.CopyPasteRequest;
import ru.adel.encoder.Encoder;

import java.nio.charset.StandardCharsets;

public class CopyPasteRequestEncoder implements Encoder {
    @Override
    public void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        CopyPasteRequest copyPasteRequest = (CopyPasteRequest) msg;
        out.writeInt(msg.getCommandType().ordinal());
        out.writeInt(copyPasteRequest.getCopyFromPath().length());
        out.writeInt(copyPasteRequest.getPasteToPath().length());
        out.writeInt(copyPasteRequest.getFilename().length());
        out.writeCharSequence(copyPasteRequest.getCopyFromPath(), StandardCharsets.UTF_8);
        out.writeCharSequence(copyPasteRequest.getPasteToPath(), StandardCharsets.UTF_8);
        out.writeCharSequence(copyPasteRequest.getFilename(), StandardCharsets.UTF_8);
    }
}
