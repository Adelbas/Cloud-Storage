package ru.adel.server.command.file;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.command.UnknownCmdResponse;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.command.CommandService;
import ru.adel.server.command.file.executors.FileUploadRequestExecutor;
import ru.adel.server.service.FileService;

@Slf4j
public class FileCommandService extends CommandService {

    public FileCommandService(FileService fileService) {
        this.executors.put(CommandType.FILE_UPLOAD_REQUEST, new FileUploadRequestExecutor(fileService));
    }

    @Override
    public void handleCommand(ChannelHandlerContext ctx, Command command) {
        CommandExecutor executor = executors.get(command.getCommandType());
        if (executor == null) {
            log.info("No executor found in command handler {} to command from " + ctx.channel().id(), this.getClass().getName());
            ctx.writeAndFlush(UnknownCmdResponse.builder().build());
        } else {
            executor.execute(ctx, command);
        }
    }
}
