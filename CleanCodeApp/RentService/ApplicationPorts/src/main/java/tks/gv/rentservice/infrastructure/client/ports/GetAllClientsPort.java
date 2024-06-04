package tks.gv.rentservice.infrastructure.client.ports;

import tks.gv.rentservice.Client;

import java.util.List;

public interface GetAllClientsPort {

    List<Client> getAllClients();
}
