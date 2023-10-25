package ru.adel.server.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adel.Command;
import ru.adel.command.AuthRequest;
import ru.adel.command.AuthResponse;
import ru.adel.server.entity.User;
import ru.adel.server.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelStorageService channelStorageService;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    void testAuthenticate_ifChannelWasAuthenticated() {
        Channel channel = mock(Channel.class);
        AuthRequest authRequest = mock(AuthRequest.class);
        int attemptsLeft = 1;
        when(channelStorageService.isChannelAuthenticated(channel)).thenReturn(true);
        when(channelStorageService.getAuthenticationAttemptsLeft(channel)).thenReturn(attemptsLeft);
        final AuthResponse expectedResponse = AuthResponse.builder()
                .isAuthenticated(true)
                .attemptsLeft(attemptsLeft)
                .build();

        Command actualResponse = authenticationService.authenticate(channel, authRequest);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void testAuthenticate_ifChannelWasNotAuthenticated_hasAttemptsLeft_isValidCredentials() {
        Channel channel = mock(Channel.class);
        final User user = User.builder()
                .username("username")
                .password("password")
                .build();
        final AuthRequest authRequest = AuthRequest.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
        int attemptsLeft = 1;
        when(channelStorageService.isChannelAuthenticated(channel)).thenReturn(false);
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(channelStorageService.hasAuthenticationAttemptsLeft(channel)).thenReturn(true);
        when(channelStorageService.getAuthenticationAttemptsLeft(channel)).thenReturn(attemptsLeft);
        final AuthResponse expectedResponse = AuthResponse.builder()
                .isAuthenticated(true)
                .attemptsLeft(attemptsLeft)
                .build();

        Command actualResponse = authenticationService.authenticate(channel, authRequest);

        Assertions.assertAll(
                "Grouped assertions of authentication response",
                () -> assertThat(actualResponse.getCommandType()).isEqualTo(expectedResponse.getCommandType()),
                () -> assertThat(((AuthResponse)actualResponse).isAuthenticated()).isEqualTo(expectedResponse.isAuthenticated()),
                () -> assertThat(((AuthResponse)actualResponse).getAttemptsLeft()).isEqualTo(expectedResponse.getAttemptsLeft())
        );
    }

    @Test
    void testAuthenticate_ifChannelWasNotAuthenticated_hasAttemptsLeft_isInvalidCredentials() {
        Channel channel = mock(Channel.class);
        final User user = User.builder()
                .username("username")
                .password("password")
                .build();
        final AuthRequest authRequest = AuthRequest.builder()
                .username(user.getUsername())
                .password("invalid_password")
                .build();
        int attemptsLeft = 2;
        when(channelStorageService.isChannelAuthenticated(channel)).thenReturn(false);
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(channelStorageService.hasAuthenticationAttemptsLeft(channel)).thenReturn(true);
        when(channelStorageService.getAuthenticationAttemptsLeft(channel)).thenReturn(attemptsLeft - 1);
        final AuthResponse expectedResponse = AuthResponse.builder()
                .isAuthenticated(false)
                .attemptsLeft(attemptsLeft - 1)
                .build();

        Command actualResponse = authenticationService.authenticate(channel, authRequest);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(channelStorageService, times(1)).decreaseAuthenticationAttempt(channel);
    }

    @Test
    void testAuthenticate_ifChannelWasNotAuthenticated_hasNoAttemptsLeft_isInvalidCredentials() {
        Channel channel = mock(Channel.class);
        ChannelPipeline channelPipeline = mock(ChannelPipeline.class);
        when(channel.pipeline()).thenReturn(channelPipeline);

        final User user = User.builder()
                .username("username")
                .password("password")
                .build();
        final AuthRequest authRequest = AuthRequest.builder()
                .username(user.getUsername())
                .password("invalid_password")
                .build();
        int attemptsLeft = 1;
        when(channelStorageService.isChannelAuthenticated(channel)).thenReturn(false);
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(channelStorageService.hasAuthenticationAttemptsLeft(channel)).thenReturn(false);
        when(channelStorageService.getAuthenticationAttemptsLeft(channel)).thenReturn(attemptsLeft - 1);
        final AuthResponse expectedResponse = AuthResponse.builder()
                .isAuthenticated(false)
                .attemptsLeft(attemptsLeft - 1)
                .build();

        Command actualResponse = authenticationService.authenticate(channel, authRequest);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(channelStorageService, times(1)).decreaseAuthenticationAttempt(channel);
    }
}