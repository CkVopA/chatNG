package skvortsov.best.pupil.chat.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import skvortsov.best.pupil.chat.client.StartClient;
import skvortsov.best.pupil.chat.client.models.Network;

import java.io.IOException;

public class AuthController {

    @FXML
    private Button signInButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;
    private Network network;
    private StartClient startClient;

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setStartClient(StartClient startClient) {
        this.startClient = startClient;
    }

    @FXML
    public void checkAuth() throws IOException {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.length() == 0 || password.length() == 0){
            startClient.showErrorAlert("Ошибка ввода данных!", "Поля не должны быть пустыми!");
            return;
        }

        String authErrorMessage = network.sendAuthMessage(login, password);

        if (authErrorMessage == null){
            startClient.createChatDialog();
            startClient.openChatDialog();
            network.setLogin(login);

        } else {
            startClient.showErrorAlert("Ошибка аутентификации!", authErrorMessage);
        }
    }

}
