package skvortsov.best.pupil.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ServerApp {

    public static final int SERVER_PORT = 8888;
    private static DataInputStream inS;
    private static DataOutputStream out;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                System.out.println("Waiting for clients to connect...");

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client is online!");

                inS = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());


                try {
                    msgFromServer();
                    waitingMsgFromClient();
                } catch (SocketException e) {
                    e.printStackTrace();
                    System.out.println("Client reset the connection");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void waitingMsgFromClient() throws IOException {
        while (true) {
            String msg = inS.readUTF();
            if (msg.equals("/stop")) {
                System.out.println("Server stopped");
                System.exit(0);
            }
            System.out.println("From client: " + msg);
            out.writeUTF("Me: " + msg);
        }
    }

    private static void msgFromServer() {
        Thread tS = new Thread(()-> {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String msgFromServer = scanner.nextLine();

            try {
                out.writeUTF("SERVER_MESSAGE: " + msgFromServer);
                System.out.println("SERVER_MESSAGE: " + msgFromServer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
        tS.setDaemon(true);
        tS.start();
    }
}
