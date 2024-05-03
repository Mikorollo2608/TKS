package tks.gv.userinterface.users.ports.clients;

import tks.gv.Client;

import java.util.List;

public interface GetClientByLoginUseCase {

    Client getClientByLogin(String login);
    List<Client> getClientByLoginMatching(String login);
}
