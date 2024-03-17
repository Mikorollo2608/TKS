package tks.gv.userinterface.users.ports.resourceadmins;

import tks.gv.users.ResourceAdmin;

import java.util.List;

public interface GetResourceAdminByLoginUseCase {

    ResourceAdmin getResourceAdminByLogin(String login);
    List<ResourceAdmin> getResourceAdminByLoginMatching(String login);
}
