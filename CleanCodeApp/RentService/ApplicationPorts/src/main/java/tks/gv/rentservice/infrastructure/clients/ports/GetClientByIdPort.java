package tks.gv.rentservice.infrastructure.clients.ports;

import tks.gv.rentservice.Client;

import java.util.UUID;

public interface GetClientByIdPort {

    Client getClientById(UUID id);
}
