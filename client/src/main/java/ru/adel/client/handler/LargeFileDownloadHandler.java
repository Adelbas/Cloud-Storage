package ru.adel.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.client.connection.Network;
import ru.adel.command.EndLargeFileTransfer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Class represents handler to download large files from server.
 * It receives file chunks as ByteByf and write it to creating file.
 */
@Slf4j
@RequiredArgsConstructor
public class LargeFileDownloadHandler extends ChannelInboundHandlerAdapter {

    private final File createdFile;

    private final long size;

    private final Network network;

    /**
     * Reads chunks from server and saves them.
     * If all data is received sends {@link EndLargeFileTransfer} command to server
     * and execute {@link Network#tearDownLargeFileDownloadConfiguration tearDownLargeFileDownloadConfiguration} method.
     *
     * @param ctx ChannelHandlerContext
     * @param msg file chunk represented as ByteBuf
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        saveLargeFilePart(byteBuf);

        if (createdFile.length() == size) {
            log.info("Saving large file {} in {} completed successfully", createdFile.getName(), createdFile.getAbsolutePath());
            var endLargeFileTransfer = EndLargeFileTransfer.builder().isSucceed(true).build();
            network.tearDownLargeFileDownloadConfiguration();
            network.sendCommand(endLargeFileTransfer);
        }
    }

    /**
     * Saves part of file in {@link LargeFileDownloadHandler#createdFile}
     *
     * @param byteBuf file data
     * @throws IOException
     */
    private void saveLargeFilePart(ByteBuf byteBuf) throws IOException {
        ByteBuffer byteBuffer = byteBuf.nioBuffer();
        RandomAccessFile randomAccessFile = new RandomAccessFile(createdFile, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        while (byteBuffer.hasRemaining()){
            fileChannel.position(createdFile.length());
            fileChannel.write(byteBuffer);
        }

        byteBuf.release();
        fileChannel.close();
        randomAccessFile.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Error saving large file {} in {}: {}", createdFile.getName(), createdFile.getAbsolutePath(),cause.getMessage());
        network.tearDownLargeFileDownloadConfiguration();
        ctx.writeAndFlush(EndLargeFileTransfer.builder().isSucceed(false).build());
        super.exceptionCaught(ctx, cause);
    }
}
