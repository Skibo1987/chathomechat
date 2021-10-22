package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String name) {
            this.login = login;
            this.password = password;
            this.nickname = name;
        }
    }

    private List<UserData> users;

    public SimpleAuthService() {
        this.users = new ArrayList<>();
        users.add(new UserData("qwe", "qwe", "qwe"));
        users.add(new UserData("asd", "asd", "asd"));
        users.add(new UserData("zxc", "zxc", "zxc"));
        for (int i = 1; i <= 10; i++) {
            users.add(new UserData("login" + 1, "password" + i, "name" + i));

        }

    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData u : users) {
            if (u.login.equals(login) && u.password.equals(password)){
                return u.nickname;
            }
        }


        return null;
    }
}
