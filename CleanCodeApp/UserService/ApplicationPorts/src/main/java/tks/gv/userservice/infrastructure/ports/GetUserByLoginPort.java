package tks.gv.userservice.infrastructure.ports;

import tks.gv.userservice.User;

import java.util.List;

public interface GetUserByLoginPort {

    User getUserByLogin(String login);

    List<User> getUserByLoginMatching(String login);
}
