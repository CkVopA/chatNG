package skvortsov.best.pupil.chat.client.models;

import skvortsov.best.pupil.chat.client.controllers.ChatController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.SimpleTimeZone;

public class Network {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + userName
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + errorMessage
    private static final String CLIENT_MSG_CMD_PREFIX = "/cmsg"; // + message from client
    private static final String SERVER_MSG_CMD_PREFIX = "/smsg"; // + message from server
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pmsg"; // + message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop"; // stop server
    private static final String END_CLIENT_CMD_PREFIX = "/end";  // end session, close connection

    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8189;
    private final String host;
    private final int port;
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;


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
            System.out.println("Message is not send");
        }
    }

    public void waitMessage(ChatController chatController){
        Thread t = new Thread(()-> {
            try {
                while (true){
                    if (socket==null) waitMessage(chatController);
                    String msg = in.readUTF();
                    chatController.appendMessage(msg);
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
