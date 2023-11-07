package ru.adel.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.server.command.CommandHandler;

/**
 * Class represents file handler.
 */
@Slf4j
@RequiredArgsConstructor
public class FileHandler extends SimpleChannelInboundHandler<Command> {

    private final CommandHandler fileCommandHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        if (!fileCommandHandler.isBelongsToHandler(command)) {
            ctx.fireChannelRead(command);
            return;
        }

        fileCommandHandler.handleCommand(ctx, command);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Exception caught in channel " + ctx.channel().id(), cause);
        ctx.close();
    }
}