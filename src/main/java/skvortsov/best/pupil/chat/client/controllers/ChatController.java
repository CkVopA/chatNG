package skvortsov.best.pupil.chat.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import skvortsov.best.pupil.chat.client.StartClient;
import skvortsov.best.pupil.chat.client.models.Network;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    public Button sendButton;
    @FXML
    private TextField inputField;

    @FXML
    private TextArea chatList;

    @FXML
    private ListView<String> contactsList;

    private final ObservableList<String> contacts = FXCollections.observableArrayList(
            "Senior","Middle","Junior"
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactsList.setItems(contacts);
        sendButton.setOnAction(actionEvent -> sendMessage());
        inputField.setOnAction(actionEvent -> sendMessage());
    }

    @FXML
    public void sendMessage(){
        String msg = inputField.getText().trim();
        inputField.clear();
        if (!msg.isBlank()) {
            network.sendMessage(msg);
            appendMessage(msg);
        }
    }

    public void appendMessage(String msg) {
        chatList.appendText(msg);
        chatList.appendText(System.lineSeparator());
    }

    @FXML
    public void clearChatList(){
        chatList.clear();
    }

    @FXML
    public void exitApp(){
        System.exit(1);
    }

    @FXML
    public void about() throws IOException {
        StartClient.windowAbout(new Stage());
    }

    private Network network;
    public void setNetwork(Network network){
        this.network = network;
    }
}