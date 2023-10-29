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

/**
 * Class represents the logic for working with files
 */
@Slf4j
@RequiredArgsConstructor
public class FileService {

    /**
     * Path where storage will be created
     */
    private static final String STORAGE_PATH = System.getProperty("user.dir") + File.separator + "storage";

    private final ChannelStorageService channelStorageService;

    /**
     * Method provides handling {@link FileMessage} command.
     * It creates directories from file path if it is needed.
     * Using {@link FileService#getActualFilePath(String, String, String) getActualFilePath} generate actual name of saving file.
     *
     * @param channel     channel from which command received
     * @param fileMessage FileMessage
     */
    public void saveFile(Channel channel, FileMessage fileMessage) {
        String filename = fileMessage.getFilename().substring(0, fileMessage.getFilename().lastIndexOf("."));
        String fileExtension = fileMessage.getFilename().substring(fileMessage.getFilename().lastIndexOf("."));

        String directory = getUserStorageDirectory(channel) + File.separator + fileMessage.getDirectory();
        Path directoryPath = Path.of(directory);

        log.info("Saving file {} to {}", filename + fileExtension, directoryPath);
        try {
            if (Files.notExists(directoryPath)) {
                directoryPath.toFile().mkdirs();
            }

            String filePath = getActualFilePath(directoryPath.toString(), filename, fileExtension);

            Files.write(Path.of(filePath), fileMessage.getData());
            log.info("Successfully saved file {} to {}", filePath.substring(filePath.lastIndexOf(File.separator) + 1), directoryPath);
        } catch (IOException e) {
            log.error("Error saving file {} from channel {}", filename + fileExtension, channel.id());
            throw new RuntimeException(e);
        }
    }

    /**
     * If file with that name exists in given path it generates new file name with adding number to end of the filename.
     *
     * @param directoryPath path where needed to check filename
     * @param filename      filename
     * @param fileExtension file extension
     * @return new path to file with edited filename
     */
    private String getActualFilePath(String directoryPath, String filename, String fileExtension) {
        String filePath = directoryPath + File.separator + filename + fileExtension;
        int fileCounter = 2;
        while (Files.exists(Path.of(filePath))) {
            filePath = directoryPath + File.separator + filename + " (" + fileCounter + ")" + fileExtension;
            fileCounter++;
        }
        return filePath;
    }

    /**
     * Overwritten method that provides same logic as {@link FileService#getActualFilePath(String, String, String) getActualFilePath} to folders
     *
     * @param directoryPath path where needed to check folder name
     * @param filename      folder name
     * @return new path to folder with edited folder name
     */
    private String getActualFilePath(String directoryPath, String filename) {
        return getActualFilePath(directoryPath, filename, "");
    }

    /**
     * Generates absolute path to current user's folder in storage
     *
     * @param channel user's channel
     * @return absolute path to user's folder in storage
     */
    private String getUserStorageDirectory(Channel channel) {
        return STORAGE_PATH + File.separator + channelStorageService.getChannelUser(channel);
    }

    /**
     * Provides getting files from folder
     *
     * @param channel       current channel
     * @param directoryPath directory to get files from
     * @return list of {@link File} objects sorted in lexicographic order
     */
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

    /**
     * Provides getting specific file
     *
     * @param channel  current channel
     * @param filePath path of file to return
     * @return {@link File file}
     */
    public File getFile(Channel channel, String filePath) {
        return new File(getUserStorageDirectory(channel) + File.separator + filePath);
    }

    /**
     * Provides creation of new folder in path
     * If folder with given name is exist generates new name with {@link FileService#getActualFilePath(String, String) getActualFilePath}
     *
     * @param channel    current channel
     * @param folderPath path where folder will be created
     * @param folderName folder name
     */
    public void createFolder(Channel channel, String folderPath, String folderName) {
        String filePath = getUserStorageDirectory(channel) + File.separator + folderPath + File.separator + folderName;
        if (Files.exists(Path.of(filePath))) {
            filePath = getActualFilePath(getUserStorageDirectory(channel) + File.separator + folderPath, folderName);
        }
        Path.of(filePath).toFile().mkdirs();
    }

    /**
     * Provides copying file from one directory to another
     *
     * @param channel  current channel
     * @param pathFrom path from which need to copy file
     * @param pathTo   path to save file
     * @param filename filename with extension
     */
    public void copy(Channel channel, String pathFrom, String pathTo, String filename) {
        String copyFromPath = getUserStorageDirectory(channel) + File.separator + pathFrom;
        String pasteToPath = getUserStorageDirectory(channel) + File.separator + pathTo;

        String filenameWithoutExtension = filename.substring(0, filename.lastIndexOf("."));
        String fileExtension = filename.substring(filename.lastIndexOf("."));
        pasteToPath = getActualFilePath(pasteToPath, filenameWithoutExtension, fileExtension);

        log.info("Copying file {} from {} to {}", filename, copyFromPath, pasteToPath);
        try {
            Files.copy(Path.of(copyFromPath + File.separator + filename), Path.of(pasteToPath));
        } catch (IOException e) {
            log.error("Error copying file {} from {} to {}", filename, copyFromPath, pasteToPath);
            throw new RuntimeException(e);
        }
    }

    /**
     * Provides deletion of directory or file
     *
     * @param channel current channel
     * @param path    path to directory or file
     */
    public void deleteFile(Channel channel, String path) {
        Path filePath = Path.of(getUserStorageDirectory(channel) + File.separator + path);
        try {
            if (filePath.toFile().isDirectory()) {
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

