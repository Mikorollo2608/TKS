package tks.gv.userinterface.users.ports.clients;

import java.util.UUID;

public interface ChangeClientStatusUseCase {

    void activateClient(UUID id);

    void deactivateClient(UUID id);
}
