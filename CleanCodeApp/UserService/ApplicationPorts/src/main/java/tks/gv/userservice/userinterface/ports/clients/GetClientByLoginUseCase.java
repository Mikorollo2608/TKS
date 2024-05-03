package tks.gv.userservice.userinterface.ports.clients;

import tks.gv.userservice.Client;

import java.util.List;

public interface GetClientByLoginUseCase {

    Client getClientByLogin(String login);
    List<Client> getClientByLoginMatching(String login);
}
