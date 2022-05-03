package skvortsov.best.pupil.chat;

import skvortsov.best.pupil.chat.server.MyServer;

import java.io.IOException;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ServerAPP {

    public static final int DEFAULT_PORT = 8187;

    public static void main(String[] args) {
        try {
            new MyServer(DEFAULT_PORT).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
