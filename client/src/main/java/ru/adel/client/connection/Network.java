package ru.adel.client.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.client.command.CommandExecutor;
import ru.adel.client.handler.ClientHandler;
import ru.adel.decoder.CommandDecoder;
import ru.adel.encoder.CommandEncoder;

import java.util.concurrent.ExecutionException;

/**
 * Class represents network connection to server using {@link #host host} and {@link #port} values.
 * Uses a singleton pattern implementation because only one instance should be created.
 * To get instance of class use {@link #getInstance} method.
 */
@Slf4j
@Setter
@Getter
public class Network {

    private String host;

    private int port;

    private NioEventLoopGroup group;

    private SocketChannel channel;

    private CommandExecutor commandExecutor;

    private static volatile Network INSTANCE;

    /**
     * Double-checked locking singleton realization.
     *
     * @return Network instance
     */
    public static Network getInstance() {
        if (INSTANCE == null) {
            synchronized (Network.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Network();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Method provides new connection to server and initialize handlers.
     * Uses {@link javafx.concurrent.Task Task} class to create connection in new thread.
     */
    private void connect() {
        try {
            Task<SocketChannel> task = new Task<>() {
                @Override
                protected SocketChannel call() throws InterruptedException {
                    group = new NioEventLoopGroup();
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) {
                                    socketChannel.pipeline().addLast(
                                            new CommandDecoder(),
                                            new CommandEncoder(),
                                            new ClientHandler(commandExecutor)
                                    );
                                }
                            });
                    ChannelFuture future = b.connect(host, port).sync();
                    return (SocketChannel) future.channel();
                }

                @Override
                protected void failed() {
                    log.info("Failed to connect server");
                    group.shutdownGracefully();
                }
            };

            new Thread(task).start();
            channel = task.get();
        } catch (ExecutionException | InterruptedException e) {
            log.warn("Exception caught during connection to server", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Disconnects from current server if connected
     */
    public void disconnect() {
        if (channel != null && group != null && channel.isActive()) {
            channel.close();
            group.shutdownGracefully();
        }
    }

    /**
     * If is not connected calls {@link #connect} method.
     * Then sends command to server.
     *
     * @param cmd command to send
     */
    public void sendCommand(Command cmd) {
        if (channel == null || channel.isShutdown()) {
            connect();
        }
        channel.writeAndFlush(cmd);
    }
}

