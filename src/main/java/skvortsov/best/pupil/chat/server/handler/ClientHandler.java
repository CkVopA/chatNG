package skvortsov.best.pupil.chat.server.handler;

import skvortsov.best.pupil.chat.server.MyServer;
import skvortsov.best.pupil.chat.server.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
    private static final String ONLINE_CLIENT_CMD_PREFIX = "/con";  // + userName
    private static final String OFFLINE_CLIENT_CMD_PREFIX = "/coff";  // + userName
    private String username;

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
            } catch (IOException e) {
                e.printStackTrace();
                myServer.unSubscribe(this);
            }
        }).start();
    }

    private void authentication() throws IOException {
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

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3){
            out.writeUTF(AUTHERR_CMD_PREFIX + " Wrong format authentication's message!");
            return false;
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = myServer.getAuthenticationService();
        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null){
            if (myServer.isUsernameBusy(username)){
                out.writeUTF(AUTHERR_CMD_PREFIX + " Login >"+ login +"< is busy!");
                return false;
            }
            out.writeUTF(String.format("%s %s", AUTHOK_CMD_PREFIX, username));
            myServer.subscribe(this);
            myServer.clientIsOnlineMessage(this);
//            myServer.sendNewClientMessageForAllButOne(this, username);

            return true;
        }
        else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " WRONG login or password!");
            return false;
        }
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
                    myServer.clientIsOfflineMessage(this);
                    String msg = "You are offline."; //TODO
                    out.writeUTF(msg);
                    myServer.unSubscribe(this);
                    return;

                }  else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)){
                    readAndSendPrivateMessage(message);
 //               }  else if (message.startsWith(ONLINE_CLIENT_CMD_PREFIX)){
 //                   myServer.sendNewClientMessageForAllButOne(this, message);
//
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
        System.out.println("Received privat msg for ["+ recipient+"]");
        out.writeUTF("Me for ["+ recipient+"]: { "+ privateMessage +" }");
        myServer.privateMessage(this, recipient, privateMessage);
    }

    public void sendMessagePrivate(String senderName, String message) throws IOException {
        out.writeUTF(String.format("[%s]: * { %s }",senderName.toUpperCase(),message));
    }

    public void sendServerMessage(String message) throws IOException {
        out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
    }

    public void sendNewClientOnline(String message) throws IOException {
        out.writeUTF(String.format("%s %s", ONLINE_CLIENT_CMD_PREFIX, message));
    }
}
