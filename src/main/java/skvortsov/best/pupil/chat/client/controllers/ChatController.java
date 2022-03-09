package skvortsov.best.pupil.chat.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import skvortsov.best.pupil.chat.client.StartClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private TextField inputField;

    @FXML
    private TextArea chatList;

    @FXML
    private ListView<String> listContacts;

    private final ObservableList<String> contacts = FXCollections.observableArrayList(
            "Senior","Middle","Junior"
    );

    @FXML
    private TextField fieldNewContact;

    @FXML
    public void sendMessageToChatList(){
        String msg = inputField.getText();
        inputField.clear();
        if (!msg.isBlank()) {
            appendMessage(msg);
        }
    }

    private void appendMessage(String msg) {
        //      try {
        //          out.writeUTF(msg);
        //      }
        //      catch (IOException e) {
        //          e.printStackTrace();
        //      }
        chatList.appendText(msg + "\n");
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listContacts.setItems(contacts);

    }
}