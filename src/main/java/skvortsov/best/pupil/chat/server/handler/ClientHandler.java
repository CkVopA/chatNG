package skvortsov.best.pupil.chat.server.handler;

import skvortsov.best.pupil.chat.server.MyServer;
import skvortsov.best.pupil.chat.server.authentication.AuthenticationService;
import skvortsov.best.pupil.chat.server.authentication.DB_Authentication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler {

    private MyServer myServer;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream inS;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + userName
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + errorMessage
    private static final String CLIENT_MSG_CMD_PREFIX = "/cm"; // + message from client
    private static final String SERVER_MSG_CMD_PREFIX = "/sm"; // + message from server
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pm"; // + message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop"; // stop server
    private static final String END_CLIENT_CMD_PREFIX = "/end";  // end session, close connection
    private static final String REFRESH_CLIENTS_LIST_CMD_PREFIX = "/rcl";  // + userlist
    private static final String OFFLINE_CLIENT_CMD_PREFIX = "/coff";  // + userName
    private static final String RENAME_USER_CMD_PREFIX = "/rnm";  // + new username
    private static final String CHANGING_USERNAME_CMD_PREFIX = "/chgusn";  // + oldUsername + newUsername
    private String username;
    private String login;

    public ClientHandler(MyServer myServer, Socket socket) {
        this.myServer = myServer;
        this.clientSocket = socket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        inS = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                myServer.unSubscribe(this);
                try {
                    myServer.sendOfflineMessage(this);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }).start();
    }

    private void authentication() throws IOException, SQLException {
        while (true) {
            String message = inS.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }
            }else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Authentication error!");
                System.out.println("This client wrong try AUTH");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException, SQLException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3){
            out.writeUTF(AUTHERR_CMD_PREFIX + " Wrong format authentication's message!");
            return false;
        }
        login = parts[1];
        String password = parts[2];

        AuthenticationService auth = myServer.getAuthenticationService();
        auth.startAuthentication();
        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null){
            if (myServer.isUsernameBusy(username)){
                out.writeUTF(AUTHERR_CMD_PREFIX + " Login >"+ login +"< is busy!");
                return false;
            }
            out.writeUTF(String.format("%s %s", AUTHOK_CMD_PREFIX, username));

            auth.endAuthentication();
            joiningClient();

            return true;
        }
        else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " WRONG login or password!");
            return false;
        }
    }

    private void joiningClient() throws IOException {
        myServer.subscribe(this);
        myServer.sendOnlineMessage(this);
    }

    public String getUsername() {
        return username;
    }

    private void readMessage() throws IOException {
        while (true){
                String message = inS.readUTF();

                System.out.println("Message from ["+ getUsername()+ "] | - "+ message);
                if (message.startsWith(STOP_SERVER_CMD_PREFIX)){
                    out.writeUTF(message);
                    out.writeUTF("You are stopped this server!\n" +
                            "Connection is lost!");

                    String msg = "Client [" + this.getUsername() + "] stopped this server.\n" +
                            "Connection is lost!";
                    System.out.println(msg);
                    myServer.sendServerMessageForAllButOne(this, msg);

                    System.exit(0);

                } else if (message.startsWith(END_CLIENT_CMD_PREFIX)){
                    out.writeUTF(message);
                    myServer.sendOfflineMessage(this);
                    String msg = "You are offline.";
                    out.writeUTF(msg);
                    myServer.unSubscribe(this);
                    return;

                }  else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)){
                    readAndSendPrivateMessage(message);
                } else if (message.startsWith(RENAME_USER_CMD_PREFIX)){
                    System.out.println("Пришла переименовка!");
                    String[] parts = message.split("\\s+",2);
                    String newUsername = parts[1];
                    String oldUsername = this.getUsername();

                    AuthenticationService auth = new DB_Authentication();
                    auth.startAuthentication();
                    if (auth.changeUsername(newUsername, login)) {
                        myServer.sendServerMessageForAllButOne(this,
                                String.format("Пользователь [ %s ] сменил никнейм на [ %s ]", oldUsername, newUsername));
                        this.username = newUsername;
                        myServer.refreshContactsList();
                        auth.endAuthentication();
                        myServer.sendNewUsername(oldUsername, newUsername);
                    }

                } else {
                    myServer.broadcastMessage(message, this);
                    out.writeUTF("Me: " + message);
                }
        }
    }

    public void sendMessageForAll(String sender, String message) throws IOException {
        out.writeUTF(String.format("[%s]: %s ", sender, message));
    }

    private void readAndSendPrivateMessage(String message) throws IOException {
        String[] partsPrivateMessage = message.split("\\s+",3);
        String recipient = partsPrivateMessage[1];
        String privateMessage = partsPrivateMessage[2];
        System.out.println("Received private msg for ["+ recipient+"]");
        out.writeUTF("Me for ["+ recipient+"]: { "+ privateMessage +" }");
        myServer.privateMessage(this, recipient, privateMessage);
    }

    public void sendMessagePrivate(String senderName, String message) throws IOException {
        out.writeUTF(String.format("[%s]: * { %s }",senderName.toUpperCase(),message));
    }

    public void sendServerMessage(String message) throws IOException {
        out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
    }

    public void sendClientsList(List<ClientHandler> clientsOnline) throws IOException {
        String msg = String.format("%s %s", REFRESH_CLIENTS_LIST_CMD_PREFIX, clientsOnline.toString());
        out.writeUTF(msg);
        System.out.println(msg);
    }
    @Override
    public String toString(){return  username;}

    public void sendChangingUsernameMessage(String oldUsername, String newUsername) throws IOException {
        String msg = String.format("%s %s %s", CHANGING_USERNAME_CMD_PREFIX, oldUsername, newUsername);
        out.writeUTF(msg);
    }
}
