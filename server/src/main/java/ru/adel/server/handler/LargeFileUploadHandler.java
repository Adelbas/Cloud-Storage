package ru.adel.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.command.EndLargeFileTransfer;
import ru.adel.decoder.CommandDecoder;
import ru.adel.server.service.FileService;

/**
 * Class represents handler to upload large file from client.
 * It receives file chunks as ByteByf and write it to creating file using {@link FileService}.
 * If all chunks are received, sets default configuration for channel pipeline.
 */
@Slf4j
@RequiredArgsConstructor
public class LargeFileUploadHandler extends ChannelInboundHandlerAdapter {

    private final FileService fileService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        fileService.saveLargeFilePartNew(ctx.channel(), byteBuf);

        if (fileService.isLargeFileSavingCompleted(ctx.channel())) {
            log.info("Large file from channel {} is saved", ctx.channel().id());
            ctx.channel().pipeline().addFirst(new CommandDecoder());
            ctx.writeAndFlush(EndLargeFileTransfer.builder().isSucceed(true).build());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Error saving large file from channel {}: {}", ctx.channel().id(),cause.getMessage());
        ctx.channel().pipeline().addFirst(new CommandDecoder());
        ctx.writeAndFlush(EndLargeFileTransfer.builder().isSucceed(false).build());
        super.exceptionCaught(ctx, cause);
    }
}

