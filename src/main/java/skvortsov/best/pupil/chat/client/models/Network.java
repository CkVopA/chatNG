package skvortsov.best.pupil.chat.client.models;

import skvortsov.best.pupil.chat.client.controllers.ChatController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.SimpleTimeZone;

public class Network {
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

    public DataOutputStream getOut() {
        return out;
    }

    public void sendMessage(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message is not send");
        }
    }

    public void waitMessage(ChatController chatController){
        Thread t = new Thread(()-> {
            try {
                while (true){
                    String msg = in.readUTF();
                    chatController.appendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
