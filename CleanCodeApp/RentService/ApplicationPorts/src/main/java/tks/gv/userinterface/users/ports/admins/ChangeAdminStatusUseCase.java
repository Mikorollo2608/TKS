package tks.gv.userinterface.users.ports.admins;

import java.util.UUID;

public interface ChangeAdminStatusUseCase {

    void activateAdmin(UUID id);

    void deactivateAdmin(UUID id);

    default void activateAdmin(String id) {
        activateAdmin(UUID.fromString(id));
    }

    default void deactivateAdmin(String id) {
        deactivateAdmin(UUID.fromString(id));
    }
}
