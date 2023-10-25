package ru.adel.server.command.security.executors;


import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.LogoutRequest;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.ChannelStorageService;

/**
 * Class provides execution of logout request by closing the channel
 *
 * @see LogoutRequest
 */
@Slf4j
@RequiredArgsConstructor
public class LogoutRequestExecutor implements CommandExecutor {

    private final ChannelStorageService channelStorageService;

    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        log.info("User " + channelStorageService.getChannelUser(ctx.channel()) + " is left");
        ctx.close();
    }
}
