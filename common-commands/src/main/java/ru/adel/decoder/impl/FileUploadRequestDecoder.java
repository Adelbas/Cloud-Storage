package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.FileUploadRequest;
import ru.adel.decoder.Decoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileUploadRequestDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
//        int filenameLength = in.readInt();
//        long fileSize = in.readLong();
//        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
//                .filename(String.valueOf(in.readCharSequence(filenameLength, StandardCharsets.UTF_8)))
//                .size(fileSize)
//                .build();
//        byte[] bytes = new byte[(int) fileSize];
//        in.readBytes(bytes);
//        fileUploadRequest.setData(bytes);
//        out.add(fileUploadRequest);
        int filenameSize = in.readInt();
        String filename = String.valueOf(in.readCharSequence(filenameSize, StandardCharsets.UTF_8));
        int size = in.readInt();
        byte[] content = new byte[size];
        in.readBytes(content);
        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
                .filename(filename)
                .size(size)
                .data(content)
                .build();
        out.add(fileUploadRequest);
    }
}
