package ru.adel.server.command;

import io.netty.channel.ChannelHandlerContext;
import ru.adel.Command;

/**
 * Interface that provides execution of some command
 */
public interface CommandExecutor {
    void execute(ChannelHandlerContext ctx, Command command);
}
