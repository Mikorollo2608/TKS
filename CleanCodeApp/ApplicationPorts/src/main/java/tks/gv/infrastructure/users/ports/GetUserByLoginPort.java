package tks.gv.infrastructure.users.ports;

import tks.gv.users.User;

import java.util.List;

public interface GetUserByLoginPort {

    User getUserByLogin(String login);

    List<User> getUserByLoginMatching(String login);
}
