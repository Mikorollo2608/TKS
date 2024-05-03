package tks.gv.userservice.userinterface.ports.admins;

import tks.gv.userservice.Admin;

import java.util.List;

public interface GetAdminByLoginUseCase {

    Admin getAdminByLogin(String login);
    List<Admin> getAdminByLoginMatching(String login);
}
