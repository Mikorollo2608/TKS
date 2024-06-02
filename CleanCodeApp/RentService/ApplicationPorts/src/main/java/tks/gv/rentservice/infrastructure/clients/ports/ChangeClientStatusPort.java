package tks.gv.rentservice.infrastructure.clients.ports;

import java.util.UUID;

public interface ChangeClientStatusPort {

    void activateClient(UUID id);

    void deactivateClient(UUID id);
}
