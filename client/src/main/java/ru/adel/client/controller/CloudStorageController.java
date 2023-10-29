package ru.adel.client.controller;

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

/**
 * Class represents JavaFx controller of cloud storage page.
 * Implements CommandExecutor to execute received commands from server at cloud storage page.
 */
@Slf4j
public class CloudStorageController implements Initializable, CommandExecutor {

    private static final Map<CommandType, Consumer<Command>> commandsExecutors = new EnumMap<>(CommandType.class);

    {
        commandsExecutors.put(CommandType.FILES_GET_RESPONSE, this::handleFilesGetResponse);
        commandsExecutors.put(CommandType.FILE_MESSAGE, this::handleFileMessage);
    }

    private static final int MAX_BUFFER_SIZE = 100000000;

    /**
     * Files from this directory are displayed to user on file chooser window.
     * As a default it is user's desktop path.
     */
    private static final String INITIAL_LOCAL_STORAGE_PATH = System.getProperty("user.home") + File.separator + "Desktop";

    private static final String INITIAL_CLOUD_STORAGE_PATH = "%s";

    private static final String CLOUD_STORAGE_START_PATH = "";

    private static final String DEFAULT_NEW_FOLDER_NAME = "New folder";

    private static final String FILE_SIZE_TEXT_MB = "File size: %d MB";

    private static final String FILE_SIZE_TEXT_KB = "File size: %d KB";

    private static final String FILE_SIZE_TEXT_B = "File size: %d bytes";

    private static final long BYTE = 1L;

    private static final long KILOBYTE = 1L << 10;

    private static final long MEGABYTE = 1L << 10 << 10;

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

    private void enterDirectory(String path) {
        if (currentCloudStorageDirectory == null) {
            currentCloudStorageDirectory = new StringBuilder(path);
        } else {
            currentCloudStorageDirectory.append(File.separator).append(path);
        }
        directoryPathText.setText(String.format(INITIAL_CLOUD_STORAGE_PATH, network.getUser()) + currentCloudStorageDirectory);
    }

    private void sendGetFilesRequest(String directory) {
        FilesGetRequest filesGetRequest = FilesGetRequest.builder().directory(directory).build();
        network.sendCommand(filesGetRequest);
    }

    @Override
    public void execute(Command command) {
        commandsExecutors.get(command.getCommandType()).accept(command);
    }

    private void handleFilesGetResponse(Command command) {
        FilesGetResponse filesGetResponse = (FilesGetResponse) command;
        currentCloudStorageDirectoryFiles = filesGetResponse.getFiles().stream().collect(Collectors.toMap(File::getName, Function.identity()));

        fileSizeText.setVisible(false);
        filesListView.getItems().clear();
        filesListView.getItems().addAll(currentCloudStorageDirectoryFiles.keySet());
        filesListView.refresh();
        filesListView.getFocusModel().focus(-1);
    }

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

    @FXML
    private void onUploadButtonClick() {
        File file = fileChooser.showOpenDialog(new Stage());

        if (file == null) {
            return;
        }

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

    @FXML
    private void onDownloadButtonClick() {
        String filename = filesListView.getSelectionModel().getSelectedItem();
        if (filename == null || isDirectory(filename)) {
            return;
        }

        fileChooser.setInitialFileName(filename);
        fileToDownload = fileChooser.showSaveDialog(new Stage());

        if (fileToDownload != null) {
            FileDownloadRequest fileDownloadRequest = FileDownloadRequest.builder()
                    .filePath(currentCloudStorageDirectory + File.separator + filename)
                    .build();
            network.sendCommand(fileDownloadRequest);
        }
    }

    private boolean isDirectory(String filename) {
        return currentCloudStorageDirectoryFiles.get(filename).isDirectory();
    }

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

    @FXML
    private void onBackButtonClick() {
        int lastSeparatorIndex = currentCloudStorageDirectory.lastIndexOf(File.separator);
        if (lastSeparatorIndex == -1) {
            return;
        }

        currentCloudStorageDirectory = new StringBuilder(currentCloudStorageDirectory.substring(0, lastSeparatorIndex));
        directoryPathText.setText(String.format(INITIAL_CLOUD_STORAGE_PATH, network.getUser()) + currentCloudStorageDirectory);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

    @FXML
    private void onCopyButtonClick() {
        if (filesListView.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        copyPasteRequest = CopyPasteRequest.builder()
                .copyFromPath(currentCloudStorageDirectory.toString())
                .filename(filesListView.getSelectionModel().getSelectedItem())
                .build();
    }

    @FXML
    private void onPasteButtonClick() {
        if (copyPasteRequest == null) {
            return;
        }

        copyPasteRequest.setPasteToPath(currentCloudStorageDirectory.toString());
        network.sendCommand(copyPasteRequest);
        sendGetFilesRequest(currentCloudStorageDirectory.toString());
    }

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
