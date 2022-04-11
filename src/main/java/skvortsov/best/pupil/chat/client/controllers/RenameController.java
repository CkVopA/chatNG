package skvortsov.best.pupil.chat.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import skvortsov.best.pupil.chat.client.StartClient;
import skvortsov.best.pupil.chat.client.models.Network;

import java.io.IOException;

public class RenameController {

    @FXML
    private TextField nameField;

    private Network network;
    private String newUsername;

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void changeUsername() throws IOException {

        // или isEmpty как-нибудь
        while (nameField.getText().trim() == null){
            newUsername = nameField.getText().trim();
        }

        network.sendNewUsername(newUsername);

        closeRenameWindow();

    }

    public void closeRenameWindow() {
        StartClient startClient = new StartClient();
        startClient.closeRename();
    }
}
