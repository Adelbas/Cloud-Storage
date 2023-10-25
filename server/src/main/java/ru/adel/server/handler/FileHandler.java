package ru.adel.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.FileUploadRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class represents file handler.
 */
@Slf4j
@RequiredArgsConstructor
public class FileHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {
        FileUploadRequest fileUploadRequest = (FileUploadRequest) msg;
//        log.info(fileUploadRequest.getFilename());
//        log.info(String.valueOf(fileUploadRequest.getSize()));
        log.info(String.valueOf(fileUploadRequest.getData().length));
        Path path = Path.of(System.getProperty("user.home") + File.separator +"Desktop"+File.separator+"/newFile.mp4");
        Files.write(path, fileUploadRequest.getData());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("Channel " + ctx.channel().id() + " opened");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("Channel " + ctx.channel().id() + " closed");
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Channel " + ctx.channel().id() + " deactivated");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Exception caught in channel " + ctx.channel().id(), cause);
        ctx.close();
    }
}