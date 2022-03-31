package skvortsov.best.pupil.chat.server.authentication;

import skvortsov.best.pupil.chat.server.models.User;
import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    public static final List<User> clients = List.of(
            new User("us1", "1111", "Senior"),
            new User("us2", "2222", "Middle"),
            new User("us3", "3333", "Junior"),
            new User("us4", "4444", "HR")
            );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)){
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println("Start AUTH");
    }

    @Override
    public void changeUsername(String login, String newNickname) {
        //TODO
    }

    @Override
    public void endAuthentication() {
        System.out.println("End AUTH");
    }
}
