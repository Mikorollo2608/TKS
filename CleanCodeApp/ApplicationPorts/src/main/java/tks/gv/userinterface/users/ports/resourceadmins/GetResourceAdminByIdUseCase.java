package tks.gv.userinterface.users.ports.resourceadmins;

import tks.gv.users.ResourceAdmin;

import java.util.UUID;

public interface GetResourceAdminByIdUseCase {
    ResourceAdmin getResourceAdminById(UUID id);

    default ResourceAdmin getResourceAdminById(String id) {
        return getResourceAdminById(UUID.fromString(id));
    }
}
