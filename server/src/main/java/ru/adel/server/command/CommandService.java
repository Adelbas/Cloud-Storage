package ru.adel.server.command;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.command.UnknownCmdResponse;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
public abstract class CommandService {
    protected final Map<CommandType, CommandExecutor> executors = new EnumMap<>(CommandType.class);

    public boolean isBelongsToHandler(Command command) {
        return executors.get(command.getCommandType()) != null;
    }

    public void handleCommand(ChannelHandlerContext ctx, Command command) {
        CommandExecutor executor = executors.get(command.getCommandType());
        if (executor == null) {
            log.info("No executor found in {} to command from " + ctx.channel().id(), this.getClass().getName());
            ctx.writeAndFlush(UnknownCmdResponse.builder().build());
        } else {
            executor.execute(ctx, command);
        }
    }
}
