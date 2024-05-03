package tks.gv.infrastructure.clients.ports;

import tks.gv.Client;

import java.util.List;

public interface GetClientByLoginPort {

    Client getClientByLogin(String login);

    List<Client> getClientByLoginMatching(String login);
}
