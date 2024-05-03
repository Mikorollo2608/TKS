package tks.gv.userservice.userinterface.ports.resourceadmins;

import tks.gv.userservice.ResourceAdmin;

import java.util.UUID;

public interface GetResourceAdminByIdUseCase {
    ResourceAdmin getResourceAdminById(UUID id);

    default ResourceAdmin getResourceAdminById(String id) {
        return getResourceAdminById(UUID.fromString(id));
    }
}
