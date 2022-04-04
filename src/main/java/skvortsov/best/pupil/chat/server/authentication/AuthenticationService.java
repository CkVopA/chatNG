package skvortsov.best.pupil.chat.server.authentication;

import java.sql.SQLException;

public interface AuthenticationService {

        String getUsernameByLoginAndPassword(String login, String password) throws SQLException;
        void startAuthentication();

        boolean changeUsername(String newNickname, String login);

        void endAuthentication();
}
