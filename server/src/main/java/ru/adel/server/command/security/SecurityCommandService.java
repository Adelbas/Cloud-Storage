package ru.adel.server.command.security;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.command.UnknownCmdResponse;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.command.security.executors.AuthRequestExecutor;
import ru.adel.server.command.security.executors.LogoutRequestExecutor;
import ru.adel.server.service.AuthenticationService;
import ru.adel.server.service.ChannelStorageService;

import java.util.EnumMap;
import java.util.Map;

/**
 * Class represents service that executes security types commands and stores executor for each of them in EnumMap
 */
@Slf4j
public class SecurityCommandService {

    private final Map<CommandType, CommandExecutor> executors = new EnumMap<>(CommandType.class);

    /**
     * Constructor that create and save executors for each command
     *
     * @param authenticationService authentication service
     * @param channelStorageService channel storage service
     */
    public SecurityCommandService(AuthenticationService authenticationService, ChannelStorageService channelStorageService) {
        executors.put(CommandType.AUTHENTICATE_REQUEST, new AuthRequestExecutor(authenticationService, channelStorageService));
        executors.put(CommandType.LOGOUT_REQUEST, new LogoutRequestExecutor(channelStorageService));
    }

    /**
     * Checks if command is belong to security command service using EnumMap
     *
     * @param command command to check
     * @return true if belongs else false
     */
    public boolean isSecurityCommand(Command command) {
        return executors.get(command.getCommandType()) != null;
    }

    /**
     * Handle command using its executor.
     * If executor is not found send unknown command response.
     *
     * @param ctx     channel handler context
     * @param command command to execute
     */
    public void handleCommand(ChannelHandlerContext ctx, Command command) {
        CommandExecutor executor = executors.get(command.getCommandType());
        if (executor == null) {
            log.info("No executor found in authentication handler to command from " + ctx.channel().id());
            ctx.writeAndFlush(UnknownCmdResponse.builder().build());
        } else {
            executor.execute(ctx, command);
        }
    }
}
