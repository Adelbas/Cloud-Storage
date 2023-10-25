package ru.adel.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.client.command.CommandExecutor;
import ru.adel.client.connection.Network;
import ru.adel.client.utils.ClientUtils;
import ru.adel.command.FileUploadRequest;
import ru.adel.command.LogoutRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Class represents JavaFx controller of cloud storage page.
 * Implements CommandExecutor to execute received commands from server at cloud storage page.
 */
@Slf4j
public class CloudStorageController implements Initializable, CommandExecutor {

    /**
     * Files from this path are displayed to user in file chooser.
     * As a default it is user's desktop path.
     */
    private static final String INITIAL_USER_PATH = System.getProperty("user.home") + File.separator +"Desktop";

    /**
     * Initial path for cloud storage files view
     */
    private static final String INITIAL_CLOUD_STORAGE_PATH = "cloud-storage"+File.separator;

    private Network network;

    private FileChooser fileChooser;

    @FXML
    private ListView<String> cloudStorageFiles;

    @FXML
    private TextField cloudStoragePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Path.of(INITIAL_USER_PATH).toFile());
        cloudStoragePath.setText(INITIAL_CLOUD_STORAGE_PATH+network.getUser());
    }

    @Override
    public void execute(Command command) {
        log.info("Executing command in cloud storage controller");
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
        log.info(file.toString());

//        byte[] data = new byte[Integer.MAX_VALUE-10];
//        Random random = new Random();
//        random.nextBytes(data);
//        int read = data.length;

        byte[] data = new byte[(int)file.length()];
        int read = 0;
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            read = fileInputStream.read(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info(String.valueOf(data.length));
        log.info(String.valueOf(read));

        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
                .size(read)
                .data(data)
                .build();

//        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
//                .filename(file.getName())
//                .size(file.length())
//                .data(data)
//                .build();
//        fileUploadRequest = FileUploadRequest.builder()
//                .filename("asd")
//                .size(3)
//                .data(new byte[]{2,3,4})
//                .build();

//        log.info(fileUploadRequest.getFilename());
//        log.info(String.valueOf(fileUploadRequest.getSize()));
//        log.info(String.valueOf(fileUploadRequest.getData().length));
        network.sendCommand(fileUploadRequest);
    }

    @FXML
    private void onDownloadButtonClick() {
        File file = fileChooser.showSaveDialog(new Stage());
        log.info(file.toString());
    }

    @FXML
    private void onCopyButtonClick() {

    }

    @FXML
    private void onPasteButtonClick() {

    }

    @FXML
    private void onDeleteButtonClick() {

    }
}
