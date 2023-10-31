package ru.adel.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.netty.channel.Channel;
import ru.adel.server.entity.LargeFileInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Class represents local storage of active channels.
 * Stores authenticated channels in {@link ChannelStorageService#authenticatedChannels map} using channel as key and channel user's username as value.
 * Stores channel's count of authentication attempts left in {@link ChannelStorageService#channelAttemptsLeft map} using channel as key and number as value.
 * Stores channels current large file to save chunks into it in {@link ChannelStorageService#channelAttemptsLeft map}.
 */
@Slf4j
@RequiredArgsConstructor
public class ChannelStorageService {

    private static final Map<Channel, String> authenticatedChannels = new HashMap<>();

    private static final Map<Channel, Integer> channelAttemptsLeft = new HashMap<>();

    private static final Map<Channel, LargeFileInfo> channelLargeFileInfo = new HashMap<>();

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

    public void addLargeFileInfo(Channel channel, LargeFileInfo largeFileInfo) {
        channelLargeFileInfo.put(channel, largeFileInfo);
    }

    public LargeFileInfo getLargeFileInfo(Channel channel) {
        return channelLargeFileInfo.get(channel);
    }
}
