package ru.adel.server.command.file.executors;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.EndLargeFileTransfer;
import ru.adel.encoder.CommandEncoder;
import ru.adel.server.command.CommandExecutor;

/**
 * Provides execution of {@link EndLargeFileTransfer} command.
 * Sets default configuration for channel pipeline.
 */
@Slf4j
@RequiredArgsConstructor
public class EndLargeFileTransferExecutor implements CommandExecutor {

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        EndLargeFileTransfer endLargeFileTransfer = (EndLargeFileTransfer) command;

        if (endLargeFileTransfer.isSucceed()) {
            log.info("Large file sending to channel {} is succeed", ctx.channel().id());
        } else {
            log.info("Large file sending to channel {} is failed", ctx.channel().id());
        }

        ctx.channel().pipeline().addFirst(new CommandEncoder());
        ctx.channel().pipeline().remove(ChunkedWriteHandler.class);
    }
}
