package tks.gv.infrastructure.users.ports;

import tks.gv.Client;

import java.util.UUID;

public interface GetUserByIdPort {

    Client getUserById(UUID id);
}
