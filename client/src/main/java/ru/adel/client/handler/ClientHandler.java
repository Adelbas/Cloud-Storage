package ru.adel.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.client.command.CommandExecutor;

/**
 * Class represent handler of commands from server.
 * Has CommandExecutor field for execution received command in current scene.
 * CommandExecutor changing everytime scene is switching.
 *
 * @see CommandExecutor
 */
@Slf4j
@Setter
@AllArgsConstructor
public class ClientHandler extends SimpleChannelInboundHandler<Command> {

    private CommandExecutor commandExecutor;

    /**
     * Method executes received command with CommandExecutor.
     * Uses {@link javafx.application.Platform#runLater(Runnable) runLater}  method to execute command in JavaFx thread.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to current channel
     * @param msg the message to handle
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) {
        log.info("Received command {} from channel {}", msg.getCommandType(), ctx.channel().id());
        Platform.runLater(() -> commandExecutor.execute(msg));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Connected to channel " + ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Disconnected from channel " + ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Exception caught in channel " + ctx.channel().id(), cause);
        ctx.close();
    }
}
