package skvortsov.best.pupil.chat.server.authentication;

import java.sql.SQLException;

public interface AuthenticationService {

        String getUsernameByLoginAndPassword(String login, String password) throws SQLException;
        void startAuthentication();

        void changeUsername(String login, String newNickname);

        void endAuthentication();
}
