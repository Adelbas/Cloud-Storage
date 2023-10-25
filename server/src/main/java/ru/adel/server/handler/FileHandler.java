package ru.adel.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;

/**
 * Class represents file handler.
 */
@Slf4j
@RequiredArgsConstructor
public class FileHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {
        log.info("Message entered to file handler");
    }
}