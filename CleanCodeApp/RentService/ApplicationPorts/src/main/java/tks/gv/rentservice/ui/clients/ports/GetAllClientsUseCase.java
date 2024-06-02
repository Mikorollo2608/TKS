package tks.gv.rentservice.ui.clients.ports;

import tks.gv.rentservice.Client;

import java.util.List;

public interface GetAllClientsUseCase {

    List<Client> getAllClients();

}
