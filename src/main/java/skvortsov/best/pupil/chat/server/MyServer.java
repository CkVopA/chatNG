package skvortsov.best.pupil.chat.server;

import skvortsov.best.pupil.chat.server.authentication.AuthenticationService;
import skvortsov.best.pupil.chat.server.authentication.DB_Authentication;
import skvortsov.best.pupil.chat.server.handler.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;
    private String filePath = "src/main/resources/skvortsov/best/pupil/chat/server/historyMessages/historyChat.txt";
    private File fileHistoryChat = new File(filePath);

    public MyServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new DB_Authentication();
        clients = new ArrayList<>();
    }

    public void start() {
        System.out.println("Server started!");
        System.out.println("------------------");
        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        Socket clientSocket = waitingClientConnection();

        processClientConnection(clientSocket);
    }

    private Socket waitingClientConnection() throws IOException {
        while (true) {
            System.out.println("Waiting clients...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");
            return clientSocket;
        }
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler handler = new ClientHandler(this, clientSocket);
        handler.handle();
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        System.out.println(clients);
        sendOnlineMessage(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clients);
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessageForAll(sender.getUsername(), message);
        }
    }

    public synchronized void privateMessage(ClientHandler sender, String recipient, String msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendPrivateMessage(sender.getUsername(), msg);
            }
        }
    }

    public synchronized void sendOnlineMessage(ClientHandler clientOn) throws IOException {

        String msg = "Client [" + clientOn.getUsername() + "] is online.";

        for (ClientHandler client : clients) {
            client.sendClientsList(clients);
            if (client == clientOn) {
                continue;
            }
            client.sendServerMessage(msg);
        }
        System.out.println(msg);
    }

    public synchronized void sendOfflineMessage(ClientHandler clientOff) throws IOException {
        String msg = "Client [" + clientOff.getUsername() + "] is offline.";

        for (ClientHandler client : clients) {
            if (client == clientOff){continue;}

            client.sendServerMessage(msg);
            client.sendClientsList(clients);
        }
        System.out.println(msg);
    }

    public synchronized void sendServerMessageForAllButOne(ClientHandler sender, String msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendServerMessage(msg);
        }
        System.out.println(msg);
    }

    public synchronized void refreshContactsList() throws IOException {
        System.out.println("Обновление списка пользователей в сети.");
        for (ClientHandler client : clients) {
            client.sendClientsList(clients);
        }
    }

    public synchronized void sendNewUsername(String oldUsername, String newUsername) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(newUsername)) {
                client.sendChangingUsernameMessage(oldUsername, newUsername);
            }
        }
    }

    public void saveMessageInHistory(String msg) throws IOException {
        if (!fileHistoryChat.exists() ){
            fileHistoryChat.createNewFile();
        } else {
            try (FileOutputStream fos = new FileOutputStream(filePath, true)){
                byte[] buffer = msg.getBytes();
                fos.write(buffer, 0, buffer.length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
