package tks.gv.infrastructure.users.ports;

import tks.gv.users.User;

import java.util.List;

public interface GetUserByLoginPort {

    User getClientByLogin(String login);

    List<User> getClientByLoginMatching(String login);
}
