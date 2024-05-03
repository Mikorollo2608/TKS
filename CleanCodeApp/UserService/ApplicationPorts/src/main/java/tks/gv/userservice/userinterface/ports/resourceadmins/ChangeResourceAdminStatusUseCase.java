package tks.gv.userservice.userinterface.ports.resourceadmins;

import java.util.UUID;

public interface ChangeResourceAdminStatusUseCase {

    void activateResourceAdmin(UUID id);

    void deactivateResourceAdmin(UUID id);

    default void activateResourceAdmin(String id) {
        activateResourceAdmin(UUID.fromString(id));
    }

    default void deactivateResourceAdmin(String id) {
        deactivateResourceAdmin(UUID.fromString(id));
    }
}
