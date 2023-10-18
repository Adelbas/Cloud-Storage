package ru.adel.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.client.command.CommandExecutor;
import ru.adel.client.connection.Network;
import ru.adel.client.utils.ClientUtils;
import ru.adel.command.LogoutRequest;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class represents JavaFx controller of cloud storage page.
 * Implements CommandExecutor to execute received commands from server at cloud storage page.
 */
@Slf4j
public class CloudStorageController implements Initializable, CommandExecutor {

    private Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
    }

    /**
     * Creates logout request and sends it to server.
     * Calls {@link Network#disconnect() disconnect} method to disconnect from server.
     * Switches scene to login page using {@link ClientUtils#switchScene switchScene} method.
     */
    @FXML
    private void onButtonClick() {
        network.sendCommand(LogoutRequest.builder().build());
        network.disconnect();
        ClientUtils.switchScene("login.fxml");
    }

    @Override
    public void execute(Command command) {
        log.info("Executing command in cloud storage controller");
    }
}
