package ru.adel.server.command.security;

import ru.adel.CommandType;
import ru.adel.server.command.CommandHandler;
import ru.adel.server.command.security.executors.AuthRequestExecutor;
import ru.adel.server.command.security.executors.LogoutRequestExecutor;
import ru.adel.server.service.AuthenticationService;
import ru.adel.server.service.ChannelStorageService;

/**
 * Class represents inheritor of {@link CommandHandler CommandHandler} that executes security types commands and stores executor for each of them in EnumMap
 */
public class SecurityCommandHandler extends CommandHandler {

    /**
     * Constructor that create and save executors for each security command
     *
     * @param authenticationService authentication service
     * @param channelStorageService channel storage service
     */
    public SecurityCommandHandler(AuthenticationService authenticationService, ChannelStorageService channelStorageService) {
        this.executors.put(CommandType.AUTHENTICATE_REQUEST, new AuthRequestExecutor(authenticationService, channelStorageService));
        this.executors.put(CommandType.LOGOUT_REQUEST, new LogoutRequestExecutor(channelStorageService));
    }
}
