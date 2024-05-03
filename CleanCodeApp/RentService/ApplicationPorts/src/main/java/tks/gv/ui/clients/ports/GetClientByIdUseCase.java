package tks.gv.ui.clients.ports;

import tks.gv.Client;

import java.util.UUID;

public interface GetClientByIdUseCase {
    Client getClientById(UUID id);

    default Client getClientById(String id) {
        return getClientById(UUID.fromString(id));
    }
}
