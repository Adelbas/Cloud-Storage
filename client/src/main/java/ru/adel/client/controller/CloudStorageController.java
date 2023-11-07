package ru.adel.client.controller;

import io.netty.handler.stream.ChunkedWriteHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.client.command.CommandExecutor;
import ru.adel.client.connection.Network;
import ru.adel.client.utils.ClientUtils;
import ru.adel.command.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.netty.handler.stream.ChunkedFile;

/**
 * Class represents JavaFx controller of cloud storage page.
 * Implements CommandExecutor to execute received commands from server at cloud storage page.
 */
@Slf4j
public class CloudStorageController implements Initializable, CommandExecutor {

    /**
     * Map that contains executors of received commands
     */
    private static final Map<CommandType, Consumer<Command>> commandsExecutors = new EnumMap<>(CommandType.class);

    {
        commandsExecutors.put(CommandType.FILES_GET_RESPONSE, this::handleFilesGetResponse);
        commandsExecutors.put(CommandType.FILE_MESSAGE, this::handleFileMessage);
        commandsExecutors.put(CommandType.END_LARGE_FILE_TRANSFER, this::handleEndLargeFileTransfer);
    }

    /**
     * Files from this directory are displayed to user on file chooser window.
     * As a default it is user's desktop path.
     */
    private static final String INITIAL_LOCAL_STORAGE_PATH = System.getProperty("user.home") + File.separator + "Desktop";

    private static final String USER_CLOUD_STORAGE_PATH = "%s";

    private static final String CLOUD_STORAGE_START_PATH = "";

    private static final String DEFAULT_NEW_FOLDER_NAME = "New folder";

    private static final String FILE_SIZE_TEXT_MB = "File size: %d MB";

    private static final String FILE_SIZE_TEXT_KB = "File size: %d KB";

    private static final String FILE_SIZE_TEXT_B = "File size: %d bytes";

    private static final long BYTE = 1L;

    private static final long KILOBYTE = 1L << 10;

    private static final long MEGABYTE = 1L << 10 << 10;

    /**
     * If file size is larger than this, file will be sent using {@link ChunkedWriteHandler}
     */
    private static final long LARGE_FILE_START_SIZE = MEGABYTE;

    private Network network;

    private FileChooser fileChooser;

    private File fileToDownload;

    private CopyPasteRequest copyPasteRequest;

    private StringBuilder currentCloudStorageDirectory;

    private Map<String, File> currentCloudStorageDirectoryFiles;

    @FXML
    private ListView<String> filesListView;

    @FXML
    private TextField directoryPathText;

    @FXML
    private Text fileSizeText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Path.of(INITIAL_LOCAL_STORAGE_PATH).toFile());

        enterDirectory(CLOUD_STORAGE_START_PATH);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

    /**
     * Method provides entering directory in cloud storage.
     * It changes {@link CloudStorageController#currentCloudStorageDirectory} value and displaying path to user.
     *
     * @param path directory to enter
     */
    private void enterDirectory(String path) {
        if (currentCloudStorageDirectory == null) {
            currentCloudStorageDirectory = new StringBuilder(path);
        } else {
            currentCloudStorageDirectory.append(File.separator).append(path);
        }
        directoryPathText.setText(String.format(USER_CLOUD_STORAGE_PATH, network.getUser()) + currentCloudStorageDirectory);
    }

    /**
     * Sends {@link FilesGetRequest} to server to get files of directory
     *
     * @param directory directory to get files from
     */
    private void sendGetFilesRequest(String directory) {
        FilesGetRequest filesGetRequest = FilesGetRequest.builder().directory(directory).build();
        network.sendCommand(filesGetRequest);
    }

    @Override
    public void execute(Command command) {
        commandsExecutors.get(command.getCommandType()).accept(command);
    }

    /**
     * Handle {@link FilesGetResponse} command.
     * Update {@link CloudStorageController#currentCloudStorageDirectoryFiles} and {@link CloudStorageController#filesListView}
     *
     * @param command FilesGetResponse command
     */
    private void handleFilesGetResponse(Command command) {
        FilesGetResponse filesGetResponse = (FilesGetResponse) command;
        currentCloudStorageDirectoryFiles = filesGetResponse.getFiles().stream().collect(Collectors.toMap(File::getName, Function.identity()));

        fileSizeText.setVisible(false);
        filesListView.getItems().clear();
        filesListView.getItems().addAll(currentCloudStorageDirectoryFiles.keySet());
        filesListView.refresh();
        filesListView.getFocusModel().focus(-1);
    }

    /**
     * Handle {@link FileMessage} command.
     * Save received command to current path.
     *
     * @param command FileMessage command
     */
    private void handleFileMessage(Command command) {
        FileMessage fileMessage = (FileMessage) command;

        log.info("Saving file {} to {}", fileToDownload.getName(), fileToDownload.getAbsolutePath());
        try {
            Files.write(fileToDownload.toPath(), fileMessage.getData());
            log.info("Successfully saved file {} to {}", fileToDownload.getName(), fileToDownload.getAbsolutePath());
        } catch (IOException e) {
            log.warn("Error saving file {} from channel {}", fileToDownload.getName(), fileToDownload.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

    private void handleEndLargeFileTransfer(Command command) {
        EndLargeFileTransfer endLargeFileTransfer = (EndLargeFileTransfer) command;
        network.tearDownLargeFileUploadConfiguration();
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

    /**
     * Creates logout request and sends it to server.
     * Calls {@link Network#disconnect() disconnect} method to disconnect from server.
     * Switches scene to login page using {@link ClientUtils#switchScene switchScene} method.
     */
    @FXML
    private void onExitButtonClick() {
        network.sendCommand(LogoutRequest.builder().build());
        network.disconnect();
        ClientUtils.switchScene("login.fxml");
    }

    /**
     * Gets file user choose to upload and checks file size.
     * If file size is small sends file using {@link CloudStorageController#sendSmallFile} method
     * else sends large file using {@link CloudStorageController#sendLargeFile} method.
     */
    @FXML
    private void onUploadButtonClick() {
        File file = fileChooser.showOpenDialog(new Stage());

        if (file == null) {
            return;
        }

        if (file.length() < LARGE_FILE_START_SIZE) {
            sendSmallFile(file);
        } else {
            sendLargeFile(file);
        }
    }

    /**
     * Read bytes from chosen file and generate and send {@link FileMessage} command.
     * Sends request to get updated files list of current directory.
     *
     * @param file file to send
     */
    private void sendSmallFile(File file) {
        byte[] data = new byte[(int) file.length()];

        int read;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            read = fileInputStream.read(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileMessage fileMessage = FileMessage.builder()
                .filename(file.getName())
                .directory(currentCloudStorageDirectory.toString())
                .size(read)
                .data(data)
                .build();

        network.sendCommand(fileMessage);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

    /**
     * Generates and send {@link StartLargeFileUpload} command to server
     * and run {@link Network#setUpLargeFileUploadConfiguration setUpLargeFileUploadConfiguration}.
     * Sends large file to server chunk by chunk using {@link ChunkedWriteHandler}
     *
     * @param file file to send
     */
    private void sendLargeFile(File file) {
        network.sendCommand(StartLargeFileUpload.builder()
                .size(file.length())
                .filename(file.getName())
                .directory(currentCloudStorageDirectory.toString())
                .build());

        try {
            ChunkedFile chunkedFile = new ChunkedFile(file);

            network.setUpLargeFileUploadConfiguration(file.length());
            network.getChannel().writeAndFlush(chunkedFile);
        } catch (IOException e) {
            log.error("Error uploading large file {}: {}", file.getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks chosen file size.
     * If file size is small generates and send {@link FileDownloadRequest} command to server,
     * else generates and send {@link StartLargeFileDownload} command to server
     * and run {@link Network#setUpLargeFileDownloadConfiguration setUpLargeFileDownloadConfiguration}.
     */
    @FXML
    private void onDownloadButtonClick() {
        String filename = filesListView.getSelectionModel().getSelectedItem();
        if (filename == null || isDirectory(filename)) {
            return;
        }

        fileChooser.setInitialFileName(filename);
        fileToDownload = fileChooser.showSaveDialog(new Stage());

        if (fileToDownload == null) {
            return;
        }

        long fileToDownloadSize = currentCloudStorageDirectoryFiles.get(filename).length();
        if (fileToDownloadSize < LARGE_FILE_START_SIZE) {
            downloadSmallFile(currentCloudStorageDirectory.toString(), filename);
        } else {
            downloadLargeFile(currentCloudStorageDirectory.toString(), filename, fileToDownloadSize);
        }
    }

    private void downloadSmallFile(String directory, String filename) {
        FileDownloadRequest fileDownloadRequest = FileDownloadRequest.builder()
                .filePath(directory + File.separator + filename)
                .build();
        network.sendCommand(fileDownloadRequest);
    }

    private void downloadLargeFile(String directory, String filename, long fileSize) {
        StartLargeFileDownload startLargeFileDownload = StartLargeFileDownload.builder()
                .filePath(directory + File.separator + filename)
                .build();

        network.setUpLargeFileDownloadConfiguration(fileToDownload, fileSize);
        network.sendCommand(startLargeFileDownload);
    }

    /**
     * Checks if file is directory
     *
     * @param filename file to check
     * @return true if it is, false else
     */
    private boolean isDirectory(String filename) {
        return currentCloudStorageDirectoryFiles.get(filename).isDirectory();
    }

    /**
     * Create new item in {@link CloudStorageController#filesListView} and suggests to edit name of it.
     * Generate {@link CreateFolderRequest} with input name and send it to server.
     */
    @FXML
    private void onNewFolderButtonClick() {
        filesListView.setCellFactory(TextFieldListCell.forListView());
        filesListView.setEditable(true);
        filesListView.getItems().add(0, DEFAULT_NEW_FOLDER_NAME);
        filesListView.scrollTo(0);
        filesListView.layout();
        filesListView.edit(0);

        filesListView.setOnEditCommit(t -> {
            network.sendCommand(CreateFolderRequest.builder().folderPath(currentCloudStorageDirectory.toString()).folderName(t.getNewValue()).build());
            sendGetFilesRequest(currentCloudStorageDirectory.toString());
            filesListView.setEditable(false);
        });

        filesListView.setOnEditCancel(t -> {
            filesListView.getItems().remove(0);
            filesListView.getFocusModel().focus(-1);
            filesListView.setEditable(false);
        });
    }

    /**
     * If count of clicks is 1 show file size if file is not directory.
     * If count of clicks if 2 enter the directory if file is directory.
     *
     * @param mouseEvent mouseEvent
     */
    @FXML
    private void onMouseClickedOnListViewItem(MouseEvent mouseEvent) {
        String selectedFile = filesListView.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            return;
        }
        fileSizeText.setVisible(false);
        if (!isDirectory(selectedFile)) {
            long fileSize = currentCloudStorageDirectoryFiles.get(selectedFile).length();
            if (fileSize > MEGABYTE) {
                fileSizeText.setText(String.format(FILE_SIZE_TEXT_MB, fileSize / MEGABYTE));
            } else if (fileSize > KILOBYTE) {
                fileSizeText.setText(String.format(FILE_SIZE_TEXT_KB, fileSize / KILOBYTE));
            } else {
                fileSizeText.setText(String.format(FILE_SIZE_TEXT_B, fileSize / BYTE));
            }
            fileSizeText.setVisible(true);
        }
        if (isDirectory(selectedFile) && mouseEvent.getClickCount() == 2) {
            enterDirectory(selectedFile);
            sendGetFilesRequest(currentCloudStorageDirectory.toString());
        }
    }

    /**
     * Moves to parent directory of {@link CloudStorageController#currentCloudStorageDirectory} if it's possible
     */
    @FXML
    private void onBackButtonClick() {
        int lastSeparatorIndex = currentCloudStorageDirectory.lastIndexOf(File.separator);
        if (lastSeparatorIndex == -1) {
            return;
        }

        currentCloudStorageDirectory = new StringBuilder(currentCloudStorageDirectory.substring(0, lastSeparatorIndex));
        directoryPathText.setText(String.format(USER_CLOUD_STORAGE_PATH, network.getUser()) + currentCloudStorageDirectory);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

    /**
     * If file is selected, and it is not a directory saves filename and directory to {@link CloudStorageController#copyPasteRequest}
     */
    @FXML
    private void onCopyButtonClick() {
        String filename = filesListView.getSelectionModel().getSelectedItem();
        if (filename == null || isDirectory(filename)) {
            return;
        }
        copyPasteRequest = CopyPasteRequest.builder()
                .copyFromPath(currentCloudStorageDirectory.toString())
                .filename(filesListView.getSelectionModel().getSelectedItem())
                .build();
    }

    /**
     * If some file was copied sends {@link CopyPasteRequest} to server
     */
    @FXML
    private void onPasteButtonClick() {
        if (copyPasteRequest == null) {
            return;
        }

        copyPasteRequest.setPasteToPath(currentCloudStorageDirectory.toString());
        network.sendCommand(copyPasteRequest);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

    /**
     * If file or directory is selected sends {@link DeleteFileRequest} to server
     */
    @FXML
    private void onDeleteButtonClick() {
        String filename = filesListView.getSelectionModel().getSelectedItem();
        if (filename == null) {
            return;
        }

        DeleteFileRequest deleteFileRequest = DeleteFileRequest.builder()
                .path(currentCloudStorageDirectory + File.separator + filename)
                .build();
        network.sendCommand(deleteFileRequest);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }
}
