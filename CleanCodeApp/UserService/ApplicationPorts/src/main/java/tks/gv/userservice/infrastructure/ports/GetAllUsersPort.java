package tks.gv.userservice.infrastructure.ports;

import tks.gv.userservice.User;

import java.util.List;

public interface GetAllUsersPort {

    List<User> getAllUsers();
}
