package ru.adel.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.adel.client.connection.Network;

import java.io.IOException;

/**
 * Client application starting point.
 * Launching login page and initialize Network instance.
 */
public class ClientApplication extends Application {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private Network network;
    public static Stage primaryStage;

    @Override
    public void init() {
        network = Network.getInstance();
        network.setHost(HOST);
        network.setPort(PORT);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Cloud Storage");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> network.disconnect());
        stage.show();
        primaryStage = stage;
        network.setCommandExecutor(fxmlLoader.getController());
    }

    public static void main(String[] args) {
        launch();
    }
}