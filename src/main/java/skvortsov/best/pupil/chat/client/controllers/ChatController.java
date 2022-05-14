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

import java.io.*;
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

    private final File libDir = new File("src/main/resources/skvortsov/best/pupil/chat/client/chat_history");

    private String selectedRecipient;
    private File fileHistory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendButton.setOnAction(actionEvent -> sendMessage());
        inputField.setOnAction(actionEvent -> sendMessage());
        contactsList.setItems(contacts);

        chooseContactsListForPrivateMessage();
    }

    public void checkFileHistory(String login) {
        fileHistory = new File(libDir, "history_[" + login + "].txt");
        if (!fileHistory.exists()){
            try {
                fileHistory.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
@FXML
    public void appendMessage(String msg) {
        String timeStamp = DateFormat.getInstance().format(new Date());
        chatList.appendText(timeStamp);
        chatList.appendText(System.lineSeparator());
        chatList.appendText(msg);
        chatList.appendText(System.lineSeparator());
        chatList.appendText(System.lineSeparator());

        String msgForHistory = timeStamp + "\n"+ msg;
        writeMessageInHistory(msgForHistory, fileHistory);
    }
@FXML
    public void appendServerMessage(String serverMessage) {
        chatList.appendText(System.lineSeparator());
        chatList.appendText(System.lineSeparator());
        chatList.appendText(">>> "+ serverMessage + " <<<");
        chatList.appendText(System.lineSeparator());
        chatList.appendText(System.lineSeparator());
    }
@FXML
    private void writeMessageInHistory(String msgForHistory, File fileHistory) {
        checkFileHistory(network.getLogin());
        try (FileWriter writer = new FileWriter(fileHistory, true)){
            writer.write(msgForHistory);
            writer.append('\n');
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void readAllHistory(){
        try (FileReader reader = new FileReader(fileHistory)){
            char[] buf = new char[256];
            int c;
            while((c = reader.read(buf))>0){
                if(c < 256){
                    buf = Arrays.copyOf(buf, c);
                }
                chatList.appendText(String.valueOf(buf));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearChatList(){  // придётся переименовать - очистить поле чата
        chatList.clear();
    }
    @FXML
    public void deleteFileHistory(){  // и создать ещё один метод по очистке файла истории
        fileHistory.delete();
        clearChatList();
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
        StartClient startClient = new StartClient();
        startClient.openRename(network);
    }




    /*@FXML
    public void getHistory(File fileHistory){
        System.out.println("Дошло до сюда?");
        String filepath = fileHistory.getPath();
        try (FileInputStream fis = new FileInputStream(filepath)){
            int length = (int) new File(filepath).length();
            byte[] buffer = new byte[length];
            fis.read(buffer, 0, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/



}