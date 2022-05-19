package skvortsov.best.pupil.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skvortsov.best.pupil.chat.server.MyServer;

import java.io.IOException;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ServerAPP {

    public static final int DEFAULT_PORT = 8187;
    public static final Logger logger = LoggerFactory.getLogger(ServerAPP.class);

    public static void main(String[] args) {
        try {
            new MyServer(DEFAULT_PORT).start();
        } catch (IOException e) {
            logger.error(e.getMessage(), " Не удалось запустить сервер!");
            e.printStackTrace();
        }
    }
}
