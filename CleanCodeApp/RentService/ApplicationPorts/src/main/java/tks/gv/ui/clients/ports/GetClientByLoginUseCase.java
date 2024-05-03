package tks.gv.ui.clients.ports;

import tks.gv.Client;

import java.util.List;

public interface GetClientByLoginUseCase {

    Client getClientByLogin(String login);
    List<Client> getClientByLoginMatching(String login);
}
