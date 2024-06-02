package tks.gv.rentservice.ui.clients.ports;

import tks.gv.rentservice.Client;

import java.util.UUID;

public interface GetClientByIdUseCase {
    Client getClientById(UUID id);

    default Client getClientById(String id) {
        return getClientById(UUID.fromString(id));
    }
}
