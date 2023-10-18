package ru.adel.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import ru.adel.decoder.CommandDecoder;
import ru.adel.encoder.CommandEncoder;
import ru.adel.server.command.security.SecurityCommandService;
import ru.adel.server.handler.AuthenticationHandler;
import ru.adel.server.handler.FileHandler;
import ru.adel.server.repository.UserRepository;
import ru.adel.server.repository.UserRepositoryImpl;
import ru.adel.server.service.AuthenticationService;
import ru.adel.server.service.ChannelStorageService;

/**
 * Class that represents server starting
 */
@Slf4j
public class Server {

    private final String host;
    private final int port;
    private final SecurityCommandService securityCommandService;
    private final ChannelStorageService channelStorageService;

    /**
     * Constructor with dependency injection
     *
     * @param host                      server host value
     * @param port                      server port value
     * @param maxAuthenticationAttempts maximum number of authentication attempts that channels connecting to the server will have
     */
    public Server(String host, int port, int maxAuthenticationAttempts) {
        this.host = host;
        this.port = port;

        UserRepository userRepository = new UserRepositoryImpl();
        channelStorageService = new ChannelStorageService(maxAuthenticationAttempts);
        AuthenticationService authenticationService = new AuthenticationService(userRepository, channelStorageService);
        securityCommandService = new SecurityCommandService(authenticationService, channelStorageService);
    }

    /**
     * Method that starts server and initialize handlers
     */
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new CommandDecoder(),
                                    new CommandEncoder(),
                                    new AuthenticationHandler(securityCommandService, channelStorageService),
                                    new FileHandler()
                            );
                        }
                    });
            ChannelFuture future = b.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.warn("Exception caught during starting server", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
