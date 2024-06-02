package tks.gv.rentservice.ui.clients.ports;

import tks.gv.rentservice.Client;

import java.util.List;

public interface GetClientByLoginUseCase {

    Client getClientByLogin(String login);
    List<Client> getClientByLoginMatching(String login);
}
