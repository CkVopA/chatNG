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

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public String changeUsername() throws IOException {

        String newUsername = nameField.getText().trim();
        StartClient startClient = new StartClient();
        startClient.closeRename();
            return newUsername;
    }
}
