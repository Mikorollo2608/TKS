package tks.gv.infrastructure.users.ports;

import tks.gv.Client;

import java.util.List;

public interface GetUserByLoginPort {

    Client getUserByLogin(String login);

    List<Client> getUserByLoginMatching(String login);
}
