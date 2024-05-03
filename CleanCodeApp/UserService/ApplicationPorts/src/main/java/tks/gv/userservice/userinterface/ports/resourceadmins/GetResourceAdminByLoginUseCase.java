package tks.gv.userservice.userinterface.ports.resourceadmins;

import tks.gv.userservice.ResourceAdmin;

import java.util.List;

public interface GetResourceAdminByLoginUseCase {

    ResourceAdmin getResourceAdminByLogin(String login);
    List<ResourceAdmin> getResourceAdminByLoginMatching(String login);
}
