package skvortsov.best.pupil.chat.client.models;

import javafx.application.Platform;
import skvortsov.best.pupil.chat.client.StartClient;
import skvortsov.best.pupil.chat.client.controllers.ChatController;
import skvortsov.best.pupil.chat.client.controllers.RenameController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + userName
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + errorMessage
    private static final String CLIENT_MSG_CMD_PREFIX = "/cm"; // + message from client
    private static final String SERVER_MSG_CMD_PREFIX = "/sm"; // + message from server
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pm"; // + message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop"; // stop server
    private static final String END_CLIENT_CMD_PREFIX = "/end";  // end session, close connection
    private static final String REFRESH_CLIENTS_LIST_CMD_PREFIX = "/rcl";  // + userName
    private static final String OFFLINE_CLIENT_CMD_PREFIX = "/coff";  // + userName
    private static final String RENAME_USER_CMD_PREFIX = "/rnm";  // + new username
    private static final String CHANGING_USERNAME_CMD_PREFIX = "/chgusn";  // + oldUsername + newUsername



    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8888;
    private final String host;
    private final int port;
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private StartClient startClient;


    public Network() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }

    public Network(String host, int port){
        this.port = port;
        this.host = host;
    }

    public void connect(){
        socket = null;
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connection is successful");

        } catch (IOException e) {
            e.printStackTrace();
            startClient.showErrorAlert("Ошибка связи!", "Соединение потеряно!");
            System.out.println("Connection failed");
        }
    }

    public void sendMessage(String msg){
        try {
            out.writeUTF(msg);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            startClient.showErrorAlert("Ошибка!", "Сообщение не передано!");
            System.out.println("Message is not send");
        }
    }

    public void waitMessage(ChatController chatController){
        Thread t = new Thread(()-> {
            try {
                while (true){
                    String msg = in.readUTF();

                    if (msg.startsWith(CLIENT_MSG_CMD_PREFIX)){
                        String[] parts = msg.split("\\s+", 3);
                        String sender = parts[1];
                        String messageFromSender = parts[2];

                        Platform.runLater(()-> {
                            chatController.appendMessage(String.format("[%s]: '%s'",sender, messageFromSender));
                        });
                    }else if (msg.startsWith(SERVER_MSG_CMD_PREFIX)){
                        String[] parts = msg.split("\\s+", 2);
                        String serverMessage = parts[1];

                        Platform.runLater(()-> {
                            chatController.appendServerMessage(serverMessage);
                        });
                    } else if (msg.startsWith(REFRESH_CLIENTS_LIST_CMD_PREFIX)){
                        msg = msg.substring(msg.indexOf('[') + 1, msg.indexOf(']'));
                        String[] users = msg.split(", ");

                        Platform.runLater(()-> chatController.updateContactsList(users));

                    } else if (msg.startsWith(CHANGING_USERNAME_CMD_PREFIX)){

                        String[] users = msg.split("\\s+");
                        String oldUsername = users[1];
                        String newUsername = users[2];
                        if (changeUsername(oldUsername, newUsername)){
                            Platform.runLater(()-> {
                                chatController.setUsernameLabel(newUsername);
                            });
                        }
                    }
                    else {
                        String finalMsg = msg;
                        Platform.runLater(()-> {
                            chatController.appendMessage(finalMsg);
                        });
                    }
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private boolean changeUsername(String oldUsername, String newUsername) {
        if (this.username.equals(oldUsername)){
            this.username = newUsername;
            return true;
        }
        else {
            System.out.println("Ошибка изменения имени!");
            return false;
        }

    }

    public String sendAuthMessage(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF();
            if (response.startsWith(AUTHOK_CMD_PREFIX)){
                this.username = response.split("\\s+",2)[1];
                return null;
            } else {
                return response.split("\\s+",2)[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendPrivateMessage(String selectedRecipient, String msg) {
        sendMessage(String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, selectedRecipient, msg));
    }

    public void sendNewUsername(String newUsername) throws IOException {
           out.writeUTF(RENAME_USER_CMD_PREFIX + " " + newUsername);
    }
}
