package ru.adel.server.command.file.executors;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.DeleteFileRequest;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.FileService;

@Slf4j
@RequiredArgsConstructor
public class DeleteFileRequestExecutor implements CommandExecutor {

    private final FileService fileService;

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        DeleteFileRequest deleteFileRequest = (DeleteFileRequest) command;
        fileService.deleteFile(ctx.channel(), deleteFileRequest.getPath());
    }
}
