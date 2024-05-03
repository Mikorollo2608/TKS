package tks.gv.userservice.userinterface.ports.clients;

import tks.gv.userservice.Client;

import java.util.UUID;

public interface GetClientByIdUseCase {
    Client getClientById(UUID id);

    default Client getClientById(String id) {
        return getClientById(UUID.fromString(id));
    }
}
