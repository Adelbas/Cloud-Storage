package ru.adel.decoder.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import ru.adel.command.FilesGetResponse;
import ru.adel.decoder.Decoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class FilesGetResponseDecoder implements Decoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException, ClassNotFoundException {
        int filesCount = in.readInt();
        List<File> files = new ArrayList<>(filesCount);

        for (int i =0; i<filesCount; i++) {
            int size = in.readInt();
            byte[] data = new byte[size];

            in.readBytes(data);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInput = new ObjectInputStream(byteArrayInputStream);

            files.add((File) objectInput.readObject());

            objectInput.close();
            byteArrayInputStream.close();
        }

        out.add(FilesGetResponse.builder().files(files).build());
    }
}
