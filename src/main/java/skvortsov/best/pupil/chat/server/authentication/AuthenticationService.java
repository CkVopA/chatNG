package skvortsov.best.pupil.chat.server.authentication;

public interface AuthenticationService {

        String getUsernameByLoginAndPassword(String login, String password);
        void startAuthentication();
        void endAuthentication();
}
