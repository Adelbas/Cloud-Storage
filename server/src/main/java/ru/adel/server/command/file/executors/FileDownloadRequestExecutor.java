package ru.adel.server.command.file.executors;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.command.FileDownloadRequest;
import ru.adel.command.FileMessage;
import ru.adel.server.command.CommandExecutor;
import ru.adel.server.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class provides execution of {@link FileDownloadRequest} command
 */
@Slf4j
@RequiredArgsConstructor
public class FileDownloadRequestExecutor implements CommandExecutor {

    private final FileService fileService;

    /**
     * Gets file from storage using {@link FileService#getFile(Channel, String) getFile} and read data from it.
     * Generates and send {@link FileMessage} to client.
     *
     * @param ctx     channel handler context
     * @param command FileDownloadRequest command
     */
    @Override
    public void execute(ChannelHandlerContext ctx, Command command) {
        FileDownloadRequest fileDownloadRequest = (FileDownloadRequest) command;
        File file = fileService.getFile(ctx.channel(), fileDownloadRequest.getFilePath());

        byte[] data = new byte[(int) file.length()];

        int read;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            read = fileInputStream.read(data);
        } catch (IOException e) {
            log.warn("Exception caught during reading from file {}" + file.getName(), e.getMessage());
            throw new RuntimeException(e);
        }

        FileMessage fileMessage = FileMessage.builder()
                .filename(file.getName())
                .directory(fileDownloadRequest.getFilePath())
                .size(read)
                .data(data)
                .build();

        ctx.writeAndFlush(fileMessage);
    }
}
