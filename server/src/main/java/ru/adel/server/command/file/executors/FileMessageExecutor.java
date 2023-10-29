package ru.adel.server.command.file.executors;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.FileMessage;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.FileService;

@Slf4j
@RequiredArgsConstructor
public class FileMessageExecutor implements CommandExecutor {

    private final FileService fileService;

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        log.info("Handle file message from channel {}", ctx.channel().id());
        FileMessage fileMessage = (FileMessage) command;
        fileService.saveFile(ctx.channel(), fileMessage);
    }
}
