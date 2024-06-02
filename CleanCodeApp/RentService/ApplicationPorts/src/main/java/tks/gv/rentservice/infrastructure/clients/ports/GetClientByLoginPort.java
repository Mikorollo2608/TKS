package tks.gv.rentservice.infrastructure.clients.ports;

import tks.gv.rentservice.Client;

import java.util.List;

public interface GetClientByLoginPort {

    Client getClientByLogin(String login);

    List<Client> getClientByLoginMatching(String login);
}
