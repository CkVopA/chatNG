package skvortsov.best.pupil.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerApp {

    public static final int SERVER_PORT = 8189;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                System.out.println("Waiting for clients to connect...");

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client is online!");

                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                try {
                    while (true) {
                        String msg = in.readUTF();

                        if (msg.equals("/stop")) {
                            System.out.println("Server stopped");
                            System.exit(0);
                        }
                        System.out.println("From client: " + msg);

                        out.writeUTF("Me: " + msg);
                    }
                } catch (SocketException e) {
                    clientSocket.close();
                    System.out.println("Client is offline");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
