package ru.adel.client.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class represents middleware handler for updating progress bar for user
 */
@Slf4j
@RequiredArgsConstructor
public class ProgressHandler extends ChannelDuplexHandler {

    private final ProgressBar progressBar;

    private final Stage progressBarStage;

    private final long totalBytes;

    private long transferredBytes = 0;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf byteBuf) {
            transferredBytes += byteBuf.readableBytes();
            double progress = (double) transferredBytes / totalBytes;
            Platform.runLater(() -> progressBar.setProgress(progress));

            if (transferredBytes == totalBytes) {
                log.info("File uploading completed");
                Platform.runLater(progressBarStage::close);
            }
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf byteBuf) {
            transferredBytes += byteBuf.readableBytes();
            double progress = (double) transferredBytes / totalBytes;
            Platform.runLater(() -> progressBar.setProgress(progress));

            if (transferredBytes == totalBytes) {
                log.info("File downloading completed");
                Platform.runLater(progressBarStage::close);
            }
        }
        super.channelRead(ctx, msg);
    }
}
