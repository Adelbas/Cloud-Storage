package ru.adel.server.command.file;

import ru.adel.CommandType;
import ru.adel.server.command.CommandHandler;
import ru.adel.server.command.file.executors.*;
import ru.adel.server.service.FileService;

/**
 * Class represents inheritor of {@link CommandHandler CommandHandler} that executes file commands and stores executor for each of them in EnumMap
 */
public class FileCommandHandler extends CommandHandler {

    /**
     * Constructor that create and save executors for each file command
     *
     * @param fileService file service
     */
    public FileCommandHandler(FileService fileService) {
        this.executors.put(CommandType.FILE_MESSAGE, new FileMessageExecutor(fileService));
        this.executors.put(CommandType.FILES_GET_REQUEST, new FilesGetRequestExecutor(fileService));
        this.executors.put(CommandType.FILE_DOWNLOAD_REQUEST, new FileDownloadRequestExecutor(fileService));
        this.executors.put(CommandType.CREATE_FOLDER_REQUEST, new CreateFolderRequestExecutor(fileService));
        this.executors.put(CommandType.COPY_PASTE_REQUEST, new CopyPasteRequestExecutor(fileService));
        this.executors.put(CommandType.DELETE_FILE_REQUEST, new DeleteFileRequestExecutor(fileService));
        this.executors.put(CommandType.START_LARGE_FILE_UPLOAD, new StartLargeFileUploadExecutor(fileService));
        this.executors.put(CommandType.START_LARGE_FILE_DOWNLOAD, new StartLargeFileDownloadExecutor(fileService));
        this.executors.put(CommandType.END_LARGE_FILE_TRANSFER, new EndLargeFileTransferExecutor());
    }
}
