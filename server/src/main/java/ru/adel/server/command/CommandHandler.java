package ru.adel.server.command;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.command.UnknownCmdResponse;

import java.util.EnumMap;
import java.util.Map;

/**
 * Abstract class that executes commands and stores executor for each of them in EnumMap
 */
@Slf4j
public abstract class CommandHandler {
    protected final Map<CommandType, CommandExecutor> executors = new EnumMap<>(CommandType.class);

    /**
     * Checks if command is belong to current command handler using EnumMap
     *
     * @param command command to check
     * @return true if belongs else false
     */
    public boolean isBelongsToHandler(Command command) {
        return executors.get(command.getCommandType()) != null;
    }

    /**
     * Handle command using its executor.
     * If executor is not found send unknown command response.
     *
     * @param ctx     channel handler context
     * @param command command to execute
     */
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
