package tks.gv.rentservice.ui.clients.ports;

import java.util.UUID;

public interface ChangeClientStatusUseCase {

    void activateClient(UUID id);

    void deactivateClient(UUID id);

    default void activateClient(String id) {
        activateClient(UUID.fromString(id));
    }

    default void deactivateClient(String id) {
        deactivateClient(UUID.fromString(id));
    }
}
