package ru.adel.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import lombok.extern.slf4j.Slf4j;
import ru.adel.client.ClientApplication;
import ru.adel.client.connection.Network;

import java.io.IOException;

/**
 * Class provides switching scenes by loading them with FXMLoader from resources
 */
@Slf4j
public class ClientUtils {

    /**
     * Method create new scene from file and sets it to primary stage.
     * Also sets loaded scene's controller to Network instance.
     *
     * @throws RuntimeException if fxml file with given name was not found
     * @param fxmlFileName filename of the scene to switch to
     */
    public static void switchScene(String fxmlFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load());
            ClientApplication.primaryStage.setScene(scene);
            Network.getInstance().setCommandExecutor(fxmlLoader.getController());
        } catch (IOException e) {
            log.warn("Exception caught while switching scenes " + e);
            throw new RuntimeException(e);
        }
    }
}
