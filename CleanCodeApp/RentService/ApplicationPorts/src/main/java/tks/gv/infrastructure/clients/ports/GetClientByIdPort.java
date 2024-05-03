package tks.gv.infrastructure.clients.ports;

import tks.gv.Client;

import java.util.UUID;

public interface GetClientByIdPort {

    Client getClientById(UUID id);
}
