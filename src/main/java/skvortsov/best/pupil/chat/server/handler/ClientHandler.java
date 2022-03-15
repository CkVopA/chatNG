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
    private DataInputStream in;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + userName
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + errorMessage
    private static final String CLIENT_MSG_CMD_PREFIX = "/cmsg"; // + message from client
    private static final String SERVER_MSG_CMD_PREFIX = "/smsg"; // + message from server
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pmsg"; // + message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop"; // stop server
    private static final String END_CLIENT_CMD_PREFIX = "/end";  // end session, close connection
    private String username;

    public ClientHandler(MyServer myServer, Socket socket) {

        this.myServer = myServer;
        this.clientSocket = socket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                waitMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)){
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
            out.writeUTF(AUTHERR_CMD_PREFIX + " Authentication error!");
            return false;
        }
        String login = parts[1];
        String password = parts[2];

        checkLoginAndPassword(login, password);

        return true;
    }

    private void checkLoginAndPassword(String login, String password) throws IOException {
        AuthenticationService auth = myServer.getAuthenticationService();
        username = auth.getUsernameByLoginAndPassword(login, password);
        if (username != null){
            out.writeUTF(SERVER_MSG_CMD_PREFIX + " Authentication "+username+" success!");
        }
        else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " WRONG login or password!");
        }
    }

    private void waitMessage() {

    }
}
