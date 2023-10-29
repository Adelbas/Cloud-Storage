package ru.adel.server.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.adel.command.FileMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FileService {

    private static final String STORAGE_PATH = System.getProperty("user.dir") + File.separator + "storage";

    private final ChannelStorageService channelStorageService;

    public void saveFile(Channel channel, FileMessage fileMessage) {
        String filename = fileMessage.getFilename().substring(0, fileMessage.getFilename().lastIndexOf("."));
        String fileExtension = fileMessage.getFilename().substring(fileMessage.getFilename().lastIndexOf("."));

        String directory = getUserStorageDirectory(channel)+File.separator+ fileMessage.getDirectory();
        Path directoryPath = Path.of(directory);

        log.info("Saving file {} to {}", filename+fileExtension, directoryPath);
        try {
            if (Files.notExists(directoryPath)) {
                directoryPath.toFile().mkdirs();
            }

            String filePath = getActualFilePath(directoryPath.toString(),filename,fileExtension);

            Files.write(Path.of(filePath), fileMessage.getData());
            log.info("Successfully saved file {} to {}", filePath.substring(filePath.lastIndexOf(File.separator)+1), directoryPath);
        } catch (IOException e) {
            log.error("Error saving file {} from channel {}", filename+fileExtension, channel.id());
            throw new RuntimeException(e);
        }
    }

    private String getActualFilePath(String directoryPath, String filename, String fileExtension) {
        String filePath = directoryPath + File.separator + filename + fileExtension;
        int fileCounter = 2;
        while (Files.exists(Path.of(filePath))) {
            filePath = directoryPath + File.separator + filename + " (" + fileCounter + ")" + fileExtension;
            fileCounter++;
        }
        return filePath;
    }

    private String getActualFilePath(String directoryPath, String filename) {
        return getActualFilePath(directoryPath,filename,"");
    }

    private String getUserStorageDirectory(Channel channel) {
        return STORAGE_PATH + File.separator + channelStorageService.getChannelUser(channel);
    }

    public List<File> getFilesFromDirectory(Channel channel, String directoryPath) {
        String directory = getUserStorageDirectory(channel) + File.separator + directoryPath;
        File folder = new File(directory);

        if (folder.listFiles() == null) {
            return new ArrayList<>();
        }

        List<File> files = new ArrayList<>(List.of(folder.listFiles()));
        Collections.sort(files);

        return files;
    }

    public File getFile(Channel channel, String filePath) {
        return new File(getUserStorageDirectory(channel) + File.separator + filePath);
    }

    public void createFolder(Channel channel, String folderPath, String folderName) {
        String filePath = getUserStorageDirectory(channel) + File.separator+folderPath+File.separator+folderName;
        if (Files.exists(Path.of(filePath))) {
            filePath = getActualFilePath(getUserStorageDirectory(channel)+File.separator+folderPath, folderName);
        }
        Path.of(filePath).toFile().mkdirs();
    }

    public void copy(Channel channel, String pathFrom, String pathTo, String filename) {
        String copyFromPath;
        String pasteToPath;

        if (pathFrom.isEmpty()) {
            copyFromPath = getUserStorageDirectory(channel);
        } else {
            copyFromPath = getUserStorageDirectory(channel) + File.separator + pathFrom;
        }

        if (pathTo.isEmpty()) {
            pasteToPath = getUserStorageDirectory(channel);
        } else {
            pasteToPath = getUserStorageDirectory(channel) + File.separator + pathTo;
        }

        if (Files.isDirectory(Path.of(copyFromPath + File.separator + filename))) {
            pasteToPath = getActualFilePath(pasteToPath, filename);
        } else {
            String filenameWithoutExtension = filename.substring(0,filename.lastIndexOf("."));
            String fileExtension = filename.substring(filename.lastIndexOf("."));
            pasteToPath = getActualFilePath(pasteToPath, filenameWithoutExtension, fileExtension);
        }

        log.info("Copying file {} from {} to {}", filename, copyFromPath, pasteToPath);
        try {
            Files.copy(Path.of(copyFromPath + File.separator + filename), Path.of(pasteToPath));
        } catch (IOException e) {
            log.error("Error copying file {} from {} to {}", filename, copyFromPath, pasteToPath);
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(Channel channel, String path) {
        Path filePath = Path.of(getUserStorageDirectory(channel) + File.separator + path);
        try {
            if (filePath.toFile().isDirectory()){
                Files.walk(filePath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                log.info("Directory {} deleted", filePath);
            } else {
                Files.delete(filePath);
                log.info("File {} deleted", filePath);
            }

        } catch (IOException e) {
            log.error("Error deleting file {}", filePath);
            throw new RuntimeException(e);
        }
    }
}

