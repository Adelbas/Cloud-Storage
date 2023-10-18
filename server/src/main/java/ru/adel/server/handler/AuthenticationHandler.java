package ru.adel.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.server.command.security.SecurityCommandService;
import ru.adel.server.service.ChannelStorageService;

/**
 * Class describes authentication handler.
 * All commands to the server firstly pass through it.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationHandler extends SimpleChannelInboundHandler<Command> {

    private final SecurityCommandService securityCommandService;

    private final ChannelStorageService channelStorageService;

    /**
     * Read received command.
     * If channel was authenticated before and command type does not belong to security,
     * command is sent further through pipeline.
     * Else handles command using security command service.
     *
     * @param ctx     the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                belongs to current channel
     * @param command the message to handle
     * @see SecurityCommandService
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) {
        if (channelStorageService.isChannelAuthenticated(ctx.channel()) &&
                !securityCommandService.isSecurityCommand(command)) {
            ctx.fireChannelRead(command);
            return;
        }

        securityCommandService.handleCommand(ctx, command);
    }

    /**
     * Add new channel to local storage
     *
     * @param ctx channel handler context
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("Channel " + ctx.channel().id() + " opened");
        channelStorageService.addChannel(ctx.channel());
    }

    /**
     * Remove channel from local storage and close it
     *
     * @param ctx channel handler context
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("Channel " + ctx.channel().id() + " closed");
        channelStorageService.removeChannel(ctx.channel());
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Channel " + ctx.channel().id() + " deactivated");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Exception caught in channel " + ctx.channel().id(), cause);
        ctx.close();
    }
}
