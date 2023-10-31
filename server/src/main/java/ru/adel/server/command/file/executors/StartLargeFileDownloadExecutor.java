package ru.adel.server.command.file.executors;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.StartLargeFileDownload;
import ru.adel.command.StartLargeFileUpload;
import ru.adel.encoder.CommandEncoder;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.FileService;

import java.io.File;
import java.io.IOException;

/**
 * Provides execution of {@link StartLargeFileDownload} command.
 * Prepares channel pipeline for sending file chunks.
 */
@Slf4j
@RequiredArgsConstructor
public class StartLargeFileDownloadExecutor implements CommandExecutor {

    private final FileService fileService;

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        StartLargeFileDownload startLargeFileDownload = (StartLargeFileDownload) command;
        File file = fileService.getFile(ctx.channel(), startLargeFileDownload.getFilePath());

        ctx.channel().pipeline().remove(CommandEncoder.class);
        ctx.channel().pipeline().addLast(new ChunkedWriteHandler());

        try {
            ChunkedFile chunkedFile = new ChunkedFile(file);
            ctx.channel().writeAndFlush(chunkedFile);

        } catch (IOException e) {
            log.error("Error sending large file {} to channel {}: {}", file.getName(), ctx.channel().id(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
