package tks.gv.infrastructure.users.ports;

import tks.gv.Client;

import java.util.List;

public interface GetAllUsersPort {

    List<Client> getAllUsers();
}
