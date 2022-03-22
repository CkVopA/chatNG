package skvortsov.best.pupil.chat.server;

import javafx.application.Platform;
import skvortsov.best.pupil.chat.server.authentication.AuthenticationService;
import skvortsov.best.pupil.chat.server.authentication.BaseAuthentication;
import skvortsov.best.pupil.chat.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;

    public MyServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new BaseAuthentication();
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
        System.out.println("Waiting clients...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client is online!");
        return clientSocket;
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler handler = new ClientHandler(this, clientSocket);
        handler.handle();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
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
                client.sendMessagePrivate(sender.getUsername(), msg);
            }
        }
    }

    public  void clientIsOnlineMessage(ClientHandler newClient) throws IOException {
        String msg = "Client [" + newClient.getUsername() + "] is online.";
        sendServerMessageForAllButOne(newClient, msg);
        sendNewClientMessageForAllButOne(newClient, newClient.getUsername());
    }

    public void clientIsOfflineMessage(ClientHandler clientOffline) throws IOException {
        String msg = "Client [" + clientOffline.getUsername() + "] is offline.";
        sendServerMessageForAllButOne(clientOffline, msg);
    }

    public synchronized void sendServerMessageForAllButOne(ClientHandler clientHandler, String msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client == clientHandler) {
                continue;
            }
            client.sendServerMessage(msg);
        }
        System.out.println(msg);
    }


    public synchronized void sendNewClientMessageForAllButOne(ClientHandler clientHandler, String msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client == clientHandler) {
                continue;
            }
            client.sendNewClientOnline(msg);
        }
        System.out.println(msg);
    }
}