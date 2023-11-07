package ru.adel.server.command.file.executors;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.FilesGetRequest;
import ru.adel.command.FilesGetResponse;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.FileService;

import java.io.File;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FilesGetRequestExecutor implements CommandExecutor {

    private final FileService fileService;

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        log.info("Handle get files request from channel {}", ctx.channel().id());
        FilesGetRequest filesGetRequest = (FilesGetRequest) command;

        List<File> files = fileService.getFilesFromDirectory(ctx.channel(), filesGetRequest.getDirectory());

        ctx.writeAndFlush(FilesGetResponse.builder().files(files).build());
    }
}
