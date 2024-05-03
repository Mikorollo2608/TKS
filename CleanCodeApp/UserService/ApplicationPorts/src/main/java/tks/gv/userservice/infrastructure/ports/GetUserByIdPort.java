package tks.gv.userservice.infrastructure.ports;

import tks.gv.userservice.User;

import java.util.UUID;

public interface GetUserByIdPort {

    User getUserById(UUID id);
}
