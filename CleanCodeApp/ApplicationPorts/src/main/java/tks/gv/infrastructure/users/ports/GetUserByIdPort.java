package tks.gv.infrastructure.users.ports;

import tks.gv.users.User;

import java.util.UUID;

public interface GetUserByIdPort {

    User getUserById(UUID id);
}
