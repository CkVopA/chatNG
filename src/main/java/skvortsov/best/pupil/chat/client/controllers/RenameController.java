package skvortsov.best.pupil.chat.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
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
    public boolean changeUsername() throws IOException {

        newUsername = nameField.getText().trim();
        if (!newUsername.isBlank()) {
            network.sendNewUsername(newUsername);
            nameField.clear();
            return true;
        } else return false;
    }

}
