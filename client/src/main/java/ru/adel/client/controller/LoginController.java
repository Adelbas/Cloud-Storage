package ru.adel.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.client.command.CommandExecutor;
import ru.adel.client.connection.Network;
import ru.adel.client.utils.ClientUtils;
import ru.adel.command.AuthRequest;
import ru.adel.command.AuthResponse;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class represents JavaFx controller of login page.
 * Implements CommandExecutor to execute received commands from server at login page.
 *
 * @see CommandExecutor
 */
@Slf4j
public class LoginController implements Initializable, CommandExecutor {
    private static final String INVALID_CREDITS_TEXT = "Invalid username or password!";

    private static final String ATTEMPTS_REMAINING_TEXT = "%d attempts remaining.";

    private Network network;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text invalidCreditsText;

    @FXML
    private Text attemptsRemainingText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
    }

    /**
     * Creates authentication request and sends it to server.
     */
    @FXML
    private void onLoginButtonClick() {
        AuthRequest authenticationCommand = new AuthRequest(usernameField.getText(),passwordField.getText());
        network.sendCommand(authenticationCommand);
    }

    /**
     * Shows error message and attempts left to authenticate.
     *
     * @param attemptsLeft attempts to show
     */
    public void showInvalidCredits(int attemptsLeft) {
        invalidCreditsText.setText(INVALID_CREDITS_TEXT);
        attemptsRemainingText.setText(String.format(ATTEMPTS_REMAINING_TEXT,attemptsLeft));
        invalidCreditsText.setVisible(true);
        attemptsRemainingText.setVisible(true);
    }

    /**
     * Method executes received command.
     * If command is not authentication response it ignored.
     * If authentication is successful switching scene to cloud storage page
     * using {@link ClientUtils#switchScene switchScene} method,
     * else calls {@link #showInvalidCredits(int) showInvalidCredits} method.
     *
     * @param command command to execute
     */
    @Override
    public void execute(Command command) {
        if (command.getCommandType() != CommandType.AUTHENTICATE_RESPONSE) {
            return;
        }
        AuthResponse authResponse = (AuthResponse) command;
        if (authResponse.isAuthenticated()) {
            ClientUtils.switchScene("cloud-storage.fxml");
        } else {
            showInvalidCredits(authResponse.getAttemptsLeft());
        }
    }
}