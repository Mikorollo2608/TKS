package tks.gv.infrastructure.users.ports;

import tks.gv.users.User;

import java.util.List;

public interface GetAllUsersPort {

    List<User> getAllUsers();
}
