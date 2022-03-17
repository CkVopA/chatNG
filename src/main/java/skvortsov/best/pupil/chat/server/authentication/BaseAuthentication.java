package skvortsov.best.pupil.chat.server.authentication;

import skvortsov.best.pupil.chat.server.models.User;

import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    public static final List<User> clients = List.of(
            new User("junior", "1111", "jun"),
            new User("middle", "2222", "mid"),
            new User("senior", "3333", "sena")
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
    public void endAuthentication() {
        System.out.println("End AUTH");
    }
}
