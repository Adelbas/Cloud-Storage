package ru.adel.server.command.file.executors;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.StartLargeFileUpload;
import ru.adel.decoder.CommandDecoder;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.entity.LargeFileInfo;
import ru.adel.server.handler.LargeFileUploadHandler;
import ru.adel.server.service.FileService;

/**
 * Provides execution of {@link StartLargeFileUpload} command.
 * Prepares channel pipeline for receiving file chunks.
 */
@Slf4j
@RequiredArgsConstructor
public class StartLargeFileUploadExecutor implements CommandExecutor {

    private final FileService fileService;

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        var startLargeFileTransfer = (StartLargeFileUpload) command;

        LargeFileInfo largeFileInfo = LargeFileInfo.builder()
                .size(startLargeFileTransfer.getSize())
                .directory(startLargeFileTransfer.getDirectory())
                .filename(startLargeFileTransfer.getFilename())
                .build();

        fileService.setLargeFileInfo(ctx.channel(), largeFileInfo);
        ctx.channel().pipeline().remove(CommandDecoder.class);
        ctx.channel().pipeline().addLast(new LargeFileUploadHandler(fileService));
    }
}
