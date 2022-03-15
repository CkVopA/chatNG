package skvortsov.best.pupil.chat.server;

import skvortsov.best.pupil.chat.server.authentication.AuthenticationService;
import skvortsov.best.pupil.chat.server.authentication.BaseAuthentication;
import skvortsov.best.pupil.chat.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;

    public MyServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new BaseAuthentication();
    }

    public void start() {
        System.out.println("Server started!");
        System.out.println("------------------");
        try {
            while (true){
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

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler handler = new ClientHandler(this, clientSocket);
        handler.handle();
    }

    private Socket waitingClientConnection() throws IOException {
        System.out.println("Waiting clients...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client is online!");
        return clientSocket;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }
}
