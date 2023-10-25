package ru.adel.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * Class represents local storage of active channels.
 * Stores authenticated channels in static map using channel as key and channel user's username as value.
 * Stores channel's count of authentication attempts left in static map using channel as key and number as value.
 */
@Slf4j
@RequiredArgsConstructor
public class ChannelStorageService {

    private static final Map<Channel, String> authenticatedChannels = new HashMap<>();
    private static final Map<Channel, Integer> channelAttemptsLeft = new HashMap<>();

    private final int maxAuthenticationAttempts;

    public void addChannel(Channel channel) {
        channelAttemptsLeft.put(channel, maxAuthenticationAttempts);
    }

    public void addAuthenticatedChannel(Channel channel, String username) {
        authenticatedChannels.put(channel, username);
    }

    public void removeChannel(Channel channel) {
        authenticatedChannels.remove(channel);
        channelAttemptsLeft.remove(channel);
    }

    public boolean isChannelAuthenticated(Channel channel) {
        return authenticatedChannels.containsKey(channel);
    }

    public boolean hasAuthenticationAttemptsLeft(Channel channel) {
        return channelAttemptsLeft.get(channel) != 0;
    }

    public int getAuthenticationAttemptsLeft(Channel channel) {
        return channelAttemptsLeft.get(channel);
    }

    /**
     * Decrease authentication attempts of channel
     *
     * @param channel channel to decrease value
     */
    public void decreaseAuthenticationAttempt(Channel channel) {
        channelAttemptsLeft.computeIfPresent(channel, (key, value) -> value - 1);
    }

    public String getChannelUser(Channel channel) {
        return authenticatedChannels.get(channel);
    }
}
