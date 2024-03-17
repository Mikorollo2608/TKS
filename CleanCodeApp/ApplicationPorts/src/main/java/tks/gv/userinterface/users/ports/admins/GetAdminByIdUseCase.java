package tks.gv.userinterface.users.ports.admins;

import tks.gv.users.Admin;

import java.util.UUID;

public interface GetAdminByIdUseCase {
    Admin getAdminById(UUID id);

    default Admin getAdminById(String id) {
        return getAdminById(UUID.fromString(id));
    }
}
