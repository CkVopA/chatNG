package skvortsov.best.pupil.chat.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import skvortsov.best.pupil.chat.client.StartClient;
import skvortsov.best.pupil.chat.client.models.Network;

public class RenameController {

    @FXML
    private TextField nameField;
    private Network network;
    @FXML
    public void changeUsername(){
        String newUsername = nameField.getText();
        network = new Network();
        network.sendMessage("/rnm "+ newUsername);
        StartClient startClient = new StartClient();
        startClient.closeRename();
    }

    public void getNetwork(Network network) {
        this.network = network;
    }
}
