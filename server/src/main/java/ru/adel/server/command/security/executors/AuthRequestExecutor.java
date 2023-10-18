package ru.adel.server.command.security.executors;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.AuthRequest;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.AuthenticationService;
import ru.adel.server.service.ChannelStorageService;

/**
 * Class provides execution of authentication request
 *
 * @see AuthRequest
 */
@Slf4j
@RequiredArgsConstructor
public class AuthRequestExecutor implements CommandExecutor {

    private final AuthenticationService authenticationService;

    private final ChannelStorageService channelStorageService;

    /**
     * If channel has no authentication attempts left deactivating it.
     * Else tries to authenticate it using authentication service
     *
     * @param ctx     channel handler context
     * @param command authentication request command
     * @see AuthenticationService
     * Sending authentication result back to client
     */
    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        if (channelStorageService.hasAuthenticationAttemptsLeft(ctx.channel())) {
            log.info("Handle authentication request from channel " + ctx.channel().id());
            Command authenticationResult = authenticationService.authenticate(ctx.channel(), (AuthRequest) command);
            ctx.writeAndFlush(authenticationResult);
        } else {
            ctx.fireChannelInactive();
        }
    }
}
