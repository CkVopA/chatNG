package skvortsov.best.pupil.chat.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import skvortsov.best.pupil.chat.client.StartClient;
import skvortsov.best.pupil.chat.client.models.Network;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    public Button sendButton;
    @FXML
    private TextField inputField;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextArea chatList;

    @FXML
    private ListView<String> contactsList;

    private  ObservableList<String> contacts = FXCollections.observableArrayList(
  //          "Senior","Middle","Junior","HR"

    );

    private String selectedRecipient;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendButton.setOnAction(actionEvent -> sendMessage());
        inputField.setOnAction(actionEvent -> sendMessage());
        contactsList.setItems(contacts);

        chooseContactsListForPrivateMessage();
    }

    private void chooseContactsListForPrivateMessage() {
        contactsList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = contactsList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                    chatList.requestFocus();
                    if (!cell.isEmpty()){
                        int index = cell.getIndex();
                        if (selectionModel.getSelectedIndices().contains(index)){
                            selectionModel.clearSelection(index);
                            selectedRecipient = null;
                        }else {
                            selectionModel.select(index);
                            selectedRecipient = cell.getItem();
                        }
                        mouseEvent.consume();
                    }
            });
            return cell;
        });
    }

    @FXML
    public void sendMessage(){
        String msg = inputField.getText().trim();
        inputField.clear();
        if (!msg.isBlank()) {
            if (selectedRecipient != null){
                network.sendPrivateMessage(selectedRecipient, msg);
            } else {
                network.sendMessage(msg);
            }
        }
    }

    public void appendMessage(String msg) {
        String timeStamp = DateFormat.getInstance().format(new Date());
        chatList.appendText(timeStamp);
        chatList.appendText(System.lineSeparator());
        chatList.appendText(msg);
        chatList.appendText(System.lineSeparator());
    }

    public void appendServerMessage(String serverMessage) {
        chatList.appendText(System.lineSeparator());
        chatList.appendText(">>> "+ serverMessage + " <<<");
        chatList.appendText(System.lineSeparator());
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
    public void setUsernameLabel(String username){
        this.usernameLabel.setText(username);
    }

    @FXML
    public void about() throws IOException {
        StartClient.windowAbout(new Stage());
    }
    private Network network;

    public void setNetwork(Network network){
        this.network = network;
    }

    public void updateContactsList(String[] users) {
        Arrays.sort(users);
        for (int i = 0; i < users.length; i++) {
            if (users[i].equals(network.getUsername())){
                users[i] = "> "+ users[i] + " <";
            }
        }
        contactsList.getItems().clear();
        Collections.addAll(contactsList.getItems(), users);
    }

    @FXML
    public void changeUsername() throws IOException {
        StartClient.openRename(network);
    }
}