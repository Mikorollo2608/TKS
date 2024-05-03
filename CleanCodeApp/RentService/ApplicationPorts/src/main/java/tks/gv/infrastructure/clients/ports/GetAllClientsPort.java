package tks.gv.infrastructure.clients.ports;

import tks.gv.Client;

import java.util.List;

public interface GetAllClientsPort {

    List<Client> getAllClients();
}
