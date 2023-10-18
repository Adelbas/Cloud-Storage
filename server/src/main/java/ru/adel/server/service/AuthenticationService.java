package ru.adel.server.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.AuthRequest;
import ru.adel.command.AuthResponse;
import ru.adel.server.repository.UserRepository;

/**
 * Class represents authentication of channel using channel local storage and user repository.
 *
 * @see ChannelStorageService
 * @see UserRepository
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final ChannelStorageService channelStorageService;

    /**
     * Provides authentication of channel.
     * First it checks channel local storage if channel has been authenticated before,
     * if it's not it tries to authenticate it using data from authentication request.
     *
     * @param channel     channel to authenticate
     * @param authRequest request with authentication data
     * @return Authentication response command with boolean field of authentication result and number of remaining attempts
     */
    public Command authenticate(Channel channel, AuthRequest authRequest) {
        boolean isAuthenticated = channelStorageService.isChannelAuthenticated(channel);

        if (!isAuthenticated) {
            isAuthenticated = authenticateNewChannel(channel, authRequest);
        }

        return AuthResponse.builder()
                .isAuthenticated(isAuthenticated)
                .attemptsLeft(channelStorageService.getAuthenticationAttemptsLeft(channel))
                .build();
    }

    /**
     * Provides authentication of new channel that was not used before.
     * It uses user repository to valid data from authentication request.
     * If authentication is succeeded channel is saving as authenticated,
     * else decrease channel's authentication attempts.
     * If the channel has no authentication attempts left deactivating the channel.
     *
     * @param channel     channel to authenticate
     * @param authRequest request with authentication data
     * @return true if authentication was successful else false
     */
    private boolean authenticateNewChannel(Channel channel, AuthRequest authRequest) {
        boolean isAuthenticated = isValidCredentials(authRequest.getUsername(), authRequest.getPassword());

        if (isAuthenticated) {
            channelStorageService.addAuthenticatedChannel(channel, authRequest.getUsername());
            log.info("User " + authRequest.getUsername() + " is authenticated");
        } else {
            channelStorageService.decreaseAuthenticationAttempt(channel);
        }

        if (!channelStorageService.hasAuthenticationAttemptsLeft(channel)) {
            channel.pipeline().fireChannelInactive();
        }

        return isAuthenticated;
    }

    /**
     * Checks the credentials using user repository
     *
     * @param username username
     * @param password password
     * @return true if valid else false
     */
    private boolean isValidCredentials(String username, String password) {
        return userRepository.getUserByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }
}
