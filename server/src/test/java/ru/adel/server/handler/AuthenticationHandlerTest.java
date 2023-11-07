package ru.adel.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adel.Command;
import ru.adel.server.command.CommandHandler;
import ru.adel.server.service.ChannelStorageService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationHandlerTest {

    @Mock
    private CommandHandler securityCommandService;

    @Mock
    private ChannelStorageService channelStorageService;

    @InjectMocks
    private AuthenticationHandler authenticationHandler;

    @Test
    void testChannelRead0_isChannelAuthenticatedBefore_isNotSecurityCommand() {
        Channel channel = mock(Channel.class);
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Command command = mock(Command.class);
        when(ctx.channel()).thenReturn(channel);
        when(channelStorageService.isChannelAuthenticated(channel)).thenReturn(true);
        when(securityCommandService.isBelongsToHandler(command)).thenReturn(false);

        authenticationHandler.channelRead0(ctx, command);
        verify(ctx).fireChannelRead(command);
        verify(securityCommandService, never()).handleCommand(ctx, command);
    }

    @Test
    void testChannelRead0_isNotChannelAuthenticatedBefore() {
        Channel channel = mock(Channel.class);
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Command command = mock(Command.class);
        when(ctx.channel()).thenReturn(channel);
        when(channelStorageService.isChannelAuthenticated(channel)).thenReturn(false);

        authenticationHandler.channelRead0(ctx, command);
        verify(securityCommandService).handleCommand(ctx, command);
    }

    @Test
    void testChannelRegistered() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Channel channel = mock(Channel.class);
        when(ctx.channel()).thenReturn(channel);

        authenticationHandler.channelRegistered(ctx);
        verify(channelStorageService).addChannel(channel);
    }

    @Test
    void testChannelUnregistered() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Channel channel = mock(Channel.class);
        when(ctx.channel()).thenReturn(channel);

        authenticationHandler.channelUnregistered(ctx);
        verify(channelStorageService).removeChannel(channel);
    }
}