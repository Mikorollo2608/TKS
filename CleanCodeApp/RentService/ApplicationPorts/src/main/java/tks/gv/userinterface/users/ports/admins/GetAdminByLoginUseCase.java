package tks.gv.userinterface.users.ports.admins;

import tks.gv.users.Admin;

import java.util.List;

public interface GetAdminByLoginUseCase {

    Admin getAdminByLogin(String login);
    List<Admin> getAdminByLoginMatching(String login);
}
