package ru.adel.client.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.client.command.CommandExecutor;
import ru.adel.client.handler.ClientHandler;
import ru.adel.client.handler.LargeFileDownloadHandler;
import ru.adel.client.handler.ProgressHandler;
import ru.adel.decoder.CommandDecoder;
import ru.adel.encoder.CommandEncoder;

import java.io.File;
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

    private String user;

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

    /**
     * Sets up large file upload configuration.
     * Removes {@link CommandEncoder} from pipeline.
     * Adds {@link ProgressHandler} and {@link ChunkedWriteHandler} to pipeline.
     *
     * @param progressBar progress bar to update
     * @param fileSize full file size that will be sent
     * @param progressBarStage stage with progress bar
     */
    public void setUpLargeFileUploadConfiguration(ProgressBar progressBar, long fileSize, Stage progressBarStage) {
        channel.pipeline().remove(CommandEncoder.class);
        channel.pipeline().addLast(new ProgressHandler(progressBar, progressBarStage, fileSize));
        channel.pipeline().addLast(new ChunkedWriteHandler());
    }

    public void tearDownLargeFileUploadConfiguration() {
        channel.pipeline().remove(ChunkedWriteHandler.class);
        channel.pipeline().remove(ProgressHandler.class);
        channel.pipeline().addFirst(new CommandEncoder());
    }

    /**
     * Sets up large file download configuration.
     * Removes {@link CommandDecoder} from pipeline.
     * Adds {@link ProgressHandler} and {@link LargeFileDownloadHandler} to pipeline.
     *
     * @param createdFile file to save received chunks
     * @param expectedSize full file size that will be received
     * @param progressBar progress bar to update
     * @param progressBarStage stage with progress bar
     */
    public void setUpLargeFileDownloadConfiguration(File createdFile, long expectedSize, ProgressBar progressBar, Stage progressBarStage) {
        channel.pipeline().remove(CommandDecoder.class);
        channel.pipeline().addLast(new ProgressHandler(progressBar, progressBarStage, expectedSize));
        channel.pipeline().addLast(new LargeFileDownloadHandler(createdFile, expectedSize, this));
    }

    public void tearDownLargeFileDownloadConfiguration() {
        channel.pipeline().remove(LargeFileDownloadHandler.class);
        channel.pipeline().addFirst(new CommandDecoder());
    }
}

