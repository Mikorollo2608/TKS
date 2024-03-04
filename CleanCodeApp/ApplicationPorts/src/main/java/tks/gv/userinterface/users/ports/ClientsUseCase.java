package tks.gv.userinterface.users.ports;

import tks.gv.users.Client;

import java.util.List;

public interface ClientsUseCase {

    void registerClient(Client client);

    List<Client> getAllClients();
}
