package tks.gv.userservice.userinterface.ports.admins;

import tks.gv.userservice.Admin;

import java.util.UUID;

public interface GetAdminByIdUseCase {
    Admin getAdminById(UUID id);

    default Admin getAdminById(String id) {
        return getAdminById(UUID.fromString(id));
    }
}
