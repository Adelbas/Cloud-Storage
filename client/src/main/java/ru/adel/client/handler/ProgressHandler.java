package ru.adel.client.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Class represents middleware handler for updating progress bar for user
 */
@Slf4j
public class ProgressHandler extends ChannelDuplexHandler {

    private final ProgressBar progressBar;

    private final Stage progressBarStage;

    private final long totalBytes;

    private long transferredBytes = 0;

    public ProgressHandler(long totalBytes) {
        this.totalBytes = totalBytes;
        progressBar = new ProgressBar();
        progressBarStage = new Stage();

        setUpProgressBarStage();
    }

    /**
     * Sets up new stage to display progress bar for user while file transfer
     */
    private void setUpProgressBarStage() {
        progressBar.setProgress(0);

        final Label label = new Label();
        label.setText("File transfer");

        final VBox vBox = new VBox();
        vBox.setPrefSize(300, 150);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label, progressBar);

        Scene scene = new Scene(vBox);

        progressBarStage.setResizable(false);
        progressBarStage.initModality(Modality.APPLICATION_MODAL);
        progressBarStage.setScene(scene);
        progressBarStage.show();
    }

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
